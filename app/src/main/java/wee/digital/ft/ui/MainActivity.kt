package wee.digital.ft.ui


import androidx.navigation.NavDirections
import okhttp3.WebSocket
import wee.digital.ft.BuildConfig
import wee.digital.ft.R
import wee.digital.ft.data.repository.Shared
import wee.digital.ft.repository.dto.SocketResponse
import wee.digital.ft.repository.dto.TokenResponse
import wee.digital.ft.repository.model.DeviceInfo
import wee.digital.ft.repository.utils.CancelPaymentCode
import wee.digital.ft.repository.utils.PaymentStatusCode
import wee.digital.ft.repository.utils.SocketEvent
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.base.BaseActivity
import wee.digital.ft.ui.base.activityVM
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.payment.PaymentArg
import wee.digital.ft.ui.progress.ProgressArg
import wee.digital.ft.ui.vm.NapasVM
import wee.digital.ft.ui.vm.SharedVM
import wee.digital.ft.ui.vm.SocketVM
import wee.digital.ft.util.deleteCache

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
        val s = BuildConfig.APPLICATION_ID
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
        sharedVM.timeoutColor.observe {
            mainView.onTimeoutColorChanged(it)
        }
        sharedVM.timeoutSecond.observe {
            mainView.onTimeoutSecondChanged(it)
        }
        sharedVM.timeoutEnd.observe {
            it ?: return@observe
            onPaymentTimeout()
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
                deleteCache()
                mainVM.resetDeviceData()
                Main.mainDirection.postValue(Main.connect)
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
                mainView.onDeviceInfoChanged(it)
                navigate(Main.adv) {
                    setLaunchSingleTop()
                }
            }
        }
    }

    private fun onPaymentArgChanged(it: PaymentArg?) {
        when {
            it != null -> {
                sharedVM.clearData()
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

    private fun onPaymentTimeout() {
        sharedVM.apply {
            clearData()
            message.value = MessageArg(
                    title = "Hết thời gian thanh toán",
                    message = "Giao dịch của bạn đã quá thời gian thanh toán. Bạn vui lòng thực hiện lại giao dịch."
            )
            startTimeout(Timeout.PAYMENT_DISMISS) {
                clearData()
                navigate(Main.adv) {
                    setLaunchSingleTop()
                }
            }
        }
        navigate(Main.message)
    }

}