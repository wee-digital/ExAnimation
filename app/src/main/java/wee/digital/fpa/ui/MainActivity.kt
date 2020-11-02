package wee.digital.fpa.ui


import androidx.navigation.NavDirections
import okhttp3.WebSocket
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.GetTokenDTOResp
import wee.digital.fpa.repository.dto.SocketResponse
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.repository.utils.CancelPaymentCode
import wee.digital.fpa.repository.utils.PaymentStatusCode
import wee.digital.fpa.repository.utils.SocketEvent
import wee.digital.fpa.ui.base.BaseActivity
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.base.viewModel
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.fpa.ui.progress.ProgressArg
import wee.digital.fpa.ui.vm.NapasVM
import wee.digital.fpa.ui.vm.SharedVM
import wee.digital.fpa.ui.vm.SocketVM
import wee.digital.fpa.util.Utils

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
        mainView.onViewInit()
    }

    override fun onLiveDataObserve() {

        mainVM.rootDirection.observe {
            onRootDirectionChanged(it)
        }
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
        sharedVM.progress.observe {
            onProgressArgChanged(it)
        }
        sharedVM.deviceInfo.observe {
            onDeviceInfoChanged(it)
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

    private fun onRootDirectionChanged(it: NavDirections) {
        navigate(it) {
            setLaunchSingleTop()
        }
    }

    private fun onTokenResponseChanged(it: GetTokenDTOResp) {
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
            SocketEvent.DIMISS_PAYMENT -> {
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
                Utils.deleteCache()
                mainVM.resetDeviceData()
                Main.mainDirection.postValue(MainDirections.actionGlobalConnectFragment())
            }
        }
    }

    private fun onDeviceInfoChanged(it: DeviceInfo?) {
        when {
            it?.uid.isNullOrEmpty() -> {
                navigate(Main.connect) {
                    setLaunchSingleTop()
                }
            }
            else -> {
                mainVM.checkDeviceStatus()
                mainView.onBindDeviceInfo(it)
                navigate(Main.adv) {
                    setLaunchSingleTop()
                }
            }
        }
    }

    private fun onPaymentArgChanged(it: PaymentArg?) {
        when (it) {
            null -> {
                return
            }
            else -> {
                activityVM(SharedVM::class).clearData()
                sharedVM.payment.value = it
                navigate(Main.splash) {
                    setNoneAnim()
                    setLaunchSingleTop()
                }
            }
        }
    }

    private fun onProgressArgChanged(it: ProgressArg?) {
        when {
            it != null -> {
                navigate(Main.progress)
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