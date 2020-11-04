package wee.digital.ft.ui

import okhttp3.WebSocket
import wee.digital.ft.BuildConfig
import wee.digital.ft.R
import wee.digital.ft.repository.dto.SocketResponse
import wee.digital.ft.repository.dto.TokenResponse
import wee.digital.ft.repository.model.DeviceInfo
import wee.digital.ft.repository.utils.CancelPaymentCode
import wee.digital.ft.repository.utils.PaymentStatusCode
import wee.digital.ft.repository.utils.SocketEvent
import wee.digital.ft.shared.Config
import wee.digital.ft.shared.Shared
import wee.digital.ft.ui.base.BaseActivity
import wee.digital.ft.ui.base.activityVM
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.payment.PaymentArg
import wee.digital.ft.ui.vm.NapasVM
import wee.digital.ft.ui.vm.SharedVM
import wee.digital.ft.ui.vm.SocketVM
import wee.digital.ft.util.deleteCache
import wee.digital.ft.util.startCamera
import wee.digital.library.extension.post

class MainActivity : BaseActivity() {

    /**
     * [BaseActivity] override
     */
    override fun layoutResource(): Int {
        return R.layout.main
    }

    override fun navigationHostId(): Int {
        return R.id.mainFragment
    }

    override fun onViewCreated() {
        /*App.recordVideo = MyVideo(this)*/
        mainView.onViewInit()
        if (Config.TESTING) post(2000) {
            napasVM.paymentLiveData.value = PaymentArg.testArg
        }
    }

    override fun onLiveDataObserve() {
        mainVM.tokenResponse.observe {
            onTokenResponseChanged(it)
        }
        socketVM.webSocketLiveData.observe {
            onWebSocketChanged(it)
        }
        socketVM.responseLiveData.observe {
            onSocketResponseChanged(it)
        }
        napasVM.paymentLiveData.observe {
            onPaymentArgChanged(it)
        }
        sharedVM.syncDeviceInfo()
        sharedVM.direction.observe {
            navigate(it) { setLaunchSingleTop() }
        }
        sharedVM.message.observe {
            it ?: return@observe
            navigate(Main.message)
        }
        sharedVM.confirm.observe {
            it ?: return@observe
            navigate(Main.confirm)
        }
        sharedVM.progress.observe {
            it ?: return@observe
            navigate(Main.progress)
        }
        sharedVM.deviceInfo.observe {
            onDeviceInfoChanged(it)
        }
        sharedVM.timeoutColor.observe {
            mainView.onTimeoutColorChanged(it)
        }
        sharedVM.timeoutSecond.observe {
            mainView.onTimeoutSecondChanged(it)
        }
    }

    /**
     * [MainActivity] properties
     */
    private val sharedVM by lazy { activityVM(SharedVM::class) }

    private val socketVM by lazy { viewModel(SocketVM::class) }

    private val napasVM by lazy { viewModel(NapasVM::class) }

    private val mainVM by lazy { viewModel(MainVM::class) }

    private val mainView by lazy { MainView(this) }

    private fun onTokenResponseChanged(it: TokenResponse) {
        when (it.Code) {
            0 -> {
                socketVM.connectSocket(it.Token)
            }
            else -> {
                mainVM.checkDeviceStatusOnTimer()
            }
        }
    }

    private fun onSocketResponseChanged(it: SocketResponse) {
        when (it.event) {
            SocketEvent.HAS_PAYMENT -> {
                napasVM.getNapasClient(it)
            }
            SocketEvent.DISMISS_PAYMENT -> {
                val calledFacePay = Shared.calledFacePay.value ?: false
                val paying = Shared.paymentProcessing
                val code = when {
                    !paying -> CancelPaymentCode.SCREEN_WAITING
                    paying && calledFacePay -> CancelPaymentCode.SCREEN_PAYING
                    paying && !calledFacePay -> CancelPaymentCode.CANCEL_SUCCESS
                    else -> 0
                }
                napasVM.requestCancelPayment(code)
                if (paying) {
                    val paymentID = napasVM.paymentLiveData.value?.paymentId ?: ""
                    napasVM.updateStatusPayment(paymentID, PaymentStatusCode.CANCEL_PAYMENT)
                }
                if (paying && calledFacePay) return
            }
            SocketEvent.DELETE_CACHE -> {
                deleteCache()
                mainVM.resetDeviceData()
                sharedVM.direction.postValue(Main.connect)
            }
        }
    }

    private fun onDeviceInfoChanged(it: DeviceInfo?) {
        post(400) {
            when {
                it?.uid.isNullOrEmpty() -> {
                    sharedVM.direction.postValue(Main.connect)
                }
                else -> {
                    mainVM.checkDeviceStatus()
                    mainView.onDeviceInfoChanged(it)
                    sharedVM.direction.postValue(Main.adv)
                }
            }
        }
    }

    private fun onPaymentArgChanged(it: PaymentArg?) {
        when (it) {
            null -> {
                /* App.recordVideo?.onDoneVideo(object : MyVideo.MyVideoCallBack {
                     override fun onResult(path: String) {
                         mainVM.pushVideo(path, sharedVM.payment.value.toString())
                     }
                 })*/
            }
            else -> {
                sharedVM.payment.postValue(it)
                if (sharedVM.isSplashing) return
                sharedVM.isSplashing = true
                startCamera()
                sharedVM.onPaymentCancel()
                navigate(Main.splash) {
                    setNoneAnim()
                    setLaunchSingleTop()
                }
                /*App.recordVideo?.startVideo()*/
            }
        }
    }

    private fun onWebSocketChanged(webSocket: WebSocket?) {
        if (webSocket == null) {
            mainVM.checkDeviceStatusOnTimer()
        }
        val isDisconnected = sharedVM.deviceInfo != null && webSocket == null
        mainView.showDisconnectDialog(isDisconnected)
    }


}