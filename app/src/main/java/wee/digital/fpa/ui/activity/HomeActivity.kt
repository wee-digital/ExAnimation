package wee.digital.fpa.ui.activity

import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.app.toast
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.SocketResultResp
import wee.digital.fpa.repository.socket.Socket
import wee.digital.fpa.repository.socket.WebSocketControl
import wee.digital.fpa.repository.utils.CancelPaymentCode
import wee.digital.fpa.repository.utils.ErrCode
import wee.digital.fpa.repository.utils.PaymentStatusCode
import wee.digital.fpa.repository.utils.SocketEvent
import wee.digital.fpa.ui.base.BaseActivity
import wee.digital.fpa.ui.base.viewModel
import wee.digital.fpa.util.Utils
import java.util.concurrent.TimeUnit

class HomeActivity : BaseActivity() {

    private var mDisposable: CompositeDisposable? = null

    private val mHomeViewModel: HomeVM by lazy { viewModel(HomeVM::class) }

    override fun layoutResource(): Int = R.layout.activity_home

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
        mHomeViewModel._checkDeviceStatusCallBack.observe {
            when (it) {
                ErrCode.DEVICE_FAIL -> reGetConnectSocket()
                ErrCode.DEVICE_EXISTS -> mHomeViewModel.getToken()
                ErrCode.DEVICE_DELETE -> {
                    toast("device not exist")
                    mHomeViewModel.resetDeviceData()
                    startClear(ConnectActivity::class.java)
                }
            }
        }

        mHomeViewModel._getTokenDTOCallBack.observe {
            when (it.Code) {
                0 -> it.Token?.let { token -> connectSocket(token) }
                else -> reGetConnectSocket()
            }
        }

        mHomeViewModel._clientIdCallBack.observe {
            if (it == null) {
                toast("getClient ID fail")
                return@observe
            }
            navigateUI(it.socket, it.clientIp)
        }
    }

    private fun reGetConnectSocket() {
        val reConnect = Single.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mHomeViewModel.checkDeviceStatus() }, {})
        mDisposable?.add(reConnect)
    }

    /**
     * connect webSocket
     */
    private fun connectSocket(token: String) {
        Socket.action.connectWebSocketMonitor(token, object : WebSocketControl.WebSocketMonitorListener {

            override fun onConnected(message: String) {}

            override fun onDisconnected(message: String) {}

            override fun onClosing(message: String) {}

            override fun unKnownError(message: String) {}

            override fun onResult(message: String) {
                val data = Gson().fromJson(message, SocketResultResp::class.java)
                        ?: SocketResultResp()
                if (data.event == SocketEvent.EVENT_PAYMENT) {
                    mHomeViewModel.getClientID(data)
                    return
                }
                Utils.deleteCache(this@HomeActivity)
                navigateUI(data)
            }

            override fun onError(message: String) {
                reGetConnectSocket()
            }

        })
    }

    private fun navigateUI(data: SocketResultResp, clientId: String = "") {
        when (data.event) {
            SocketEvent.EVENT_CANCEL -> {

            }
            SocketEvent.EVENT_PAYMENT -> {
                toast("payment")
                val paying = Shared.paymentProcessing
                if (paying) {
                    mHomeViewModel.updateStatusPayment(data.paymentId, PaymentStatusCode.DEVICE_PROCESSING)
                }
                if (paying) return
                Shared.paymentProcessing = true

                App.realSenseControl?.startStreamThread()

                Shared.totalTimeFacePay.postValue(data.timeOut)

                Shared.paymentID.postValue(data.paymentId)
                Shared.amountTransaction.postValue(data.amount)
                Shared.clientID.postValue(clientId)
            }
            SocketEvent.EVENT_DELETE -> {
                toast("device not exist")
                mHomeViewModel.resetDeviceData()
                startClear(ConnectActivity::class.java)
            }
        }
    }

    private fun checkCancelPayment() {
        val calledFacePay = Shared.calledFacePay.value ?: false
        val paying = Shared.paymentProcessing
        val code = when {
            !paying -> CancelPaymentCode.SCREEN_WAITING
            paying && calledFacePay -> CancelPaymentCode.SCREEN_PAYING
            paying && !calledFacePay -> CancelPaymentCode.CANCEL_SUCCESS
            else -> 0
        }
        mHomeViewModel.updateCancelPayment(code)

        if(paying){
            val paymentID = Shared.paymentID.value ?: ""
            mHomeViewModel.updateStatusPayment(paymentID, PaymentStatusCode.CANCEL_PAYMENT)
        }

        if(paying && calledFacePay) return
        toast("go home")
    }

    /**
     * implement lifeCycle
     */
    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onPause() {
        super.onPause()
        releaseData()
    }

    private fun initData() {
        mDisposable = CompositeDisposable()
        mHomeViewModel.checkDeviceStatus()
    }

    private fun releaseData() {
        mDisposable?.dispose()
        Socket.action.closeWebSocketMonitor()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }

}