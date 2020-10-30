package wee.digital.fpa.ui


import androidx.navigation.NavDirections
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.GetTokenDTOResp
import wee.digital.fpa.repository.dto.SocketResultResp
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.repository.utils.CancelPaymentCode
import wee.digital.fpa.repository.utils.PaymentStatusCode
import wee.digital.fpa.repository.utils.SocketEvent
import wee.digital.fpa.ui.base.BaseActivity
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.face.FaceVM
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.fpa.ui.payment.PaymentVM
import wee.digital.fpa.ui.pin.PinVM
import wee.digital.fpa.ui.progress.ProgressArg
import wee.digital.fpa.ui.progress.ProgressVM
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
        /* post(1000) {
             progressVM.arg.value = ProgressArg.payment
             navigate(MainDirections.actionGlobalOtpFragment())
         }*/
    }

    override fun onLiveDataObserve() {
        mainVM.checkDeviceStatus()
        mainVM.syncDeviceInfo()
        mainVM.rootDirection.observe {
            onRootDirectionChanged(it)
        }
        mainVM.deviceInfo.observe {
            onDeviceInfoChanged(it)
        }
        mainVM.tokenResponse.observe {
            onTokenResponseChanged(it)
        }
        socketVM.webSocket.observe {
            if (it == null) {
                onCheckDeviceStatus()
            }
        }
        socketVM.response.observe {
            onSocketResponseChanged(it)
        }
        paymentVM.arg.observe {
            onPaymentArgChanged(it)
        }
        progressVM.arg.observe {
            onProgressArgChanged(it)
        }
    }


    /**
     * [MainActivity] properties
     */
    private val paymentVM by lazy { activityVM(PaymentVM::class) }

    private val socketVM by lazy { viewModel(SocketVM::class) }

    private val progressVM by lazy { viewModel(ProgressVM::class) }

    private val faceVM by lazy { activityVM(FaceVM::class) }

    private val pinVM by lazy { activityVM(PinVM::class) }

    private val mainVM by lazy { viewModel(MainVM::class) }

    private val mainView by lazy { MainView(this) }

    private fun onRootDirectionChanged(it: NavDirections) {
        navigate(it) {
            setLaunchSingleTop()
        }
    }

    private fun onCheckDeviceStatus() {
        navigate(MainDirections.actionGlobalDisconnectFragment()) {
            setEnterAnim(R.anim.vertical_enter)
            setExitAnim(R.anim.vertical_exit)
        }
        mainVM.checkDeviceStatusOnTimer()
    }

    private fun onTokenResponseChanged(it: GetTokenDTOResp) {
        when (it.Code) {
            0 -> it.Token?.let { token ->
                socketVM.connectSocket(token)
            }
            else -> {
                onCheckDeviceStatus()
            }
        }
    }

    private fun onSocketResponseChanged(it: SocketResultResp) {
        when (it.event) {
            SocketEvent.HAS_PAYMENT -> {
                paymentVM.getNapasClient(it)
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
                paymentVM.requestCancelPayment(code)
                if (paying) {
                    val paymentID = paymentVM.arg.value?.paymentId ?: ""
                    paymentVM.updateStatusPayment(paymentID, PaymentStatusCode.CANCEL_PAYMENT)
                }
                if (paying && calledFacePay) return
            }
            SocketEvent.DELETE_CACHE -> {
                Utils.deleteCache()
                mainVM.resetDeviceData()
                Main.rootDirection.postValue(MainDirections.actionGlobalConnectFragment())
            }
        }
    }

    private fun onDeviceInfoChanged(it: DeviceInfo?) {
        when {
            it?.uid.isNullOrEmpty() -> {
                navigate(MainDirections.actionGlobalConnectFragment()) {
                    setLaunchSingleTop()
                }
            }
            else -> {
                mainView.onBindDeviceInfo(it)
                navigate(MainDirections.actionGlobalAdvFragment()) {
                    setLaunchSingleTop()
                }
            }
        }
    }

    private fun onPaymentArgChanged(it: PaymentArg?) {
        faceVM.faceArg.value = null
        pinVM.pinArg.value = null
        when (it) {
            null -> navigate(MainDirections.actionGlobalAdvFragment()) {
                setNoneAnim()
                setLaunchSingleTop()
            }
            else -> navigate(MainDirections.actionGlobalSplashFragment()) {
                setNoneAnim()
                setLaunchSingleTop()
            }
        }
    }

    private fun onProgressArgChanged(it: ProgressArg?) {
        when {
            it != null -> {
                navigate(it.direction)
            }
        }
    }


}