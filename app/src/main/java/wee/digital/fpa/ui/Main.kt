package wee.digital.fpa.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import wee.digital.fpa.MainDirections
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.base.*
import wee.digital.fpa.ui.card.CardVM
import wee.digital.fpa.ui.confirm.ConfirmVM
import wee.digital.fpa.ui.connect.ConnectVM
import wee.digital.fpa.ui.face.FaceVM
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.message.MessageVM
import wee.digital.fpa.ui.otp.OtpVM
import wee.digital.fpa.ui.payment.PaymentVM
import wee.digital.fpa.ui.pin.PinVM
import wee.digital.fpa.ui.progress.ProgressVM
import wee.digital.fpa.ui.vm.TimeoutVM
import kotlin.reflect.KClass

val Fragment.messageVM get() = activityVM(MessageVM::class)

val Fragment.confirmVM get() = activityVM(ConfirmVM::class)

val Fragment.progressVM get() = activityVM(ProgressVM::class)

val Fragment.mainVM get() = activityVM(MainVM::class)

val Fragment.timeoutVM get() = activityVM(TimeoutVM::class)

val Fragment.connectVM get() = activityVM(ConnectVM::class)

val Fragment.paymentVM get() = activityVM(PaymentVM::class)

val Fragment.faceVM get() = activityVM(FaceVM::class)

val Fragment.pinVM get() = activityVM(PinVM::class)

val Fragment.cardVM get() = activityVM(CardVM::class)

val Fragment.otpVM get() = activityVM(OtpVM::class)

fun Main.Fragment<*>.onPaymentFailed(messageArg: MessageArg?) {
    progressVM.arg.postValue(null)
    paymentVM.arg.postValue(null)
    timeoutVM.startTimeout(Timeout.PAYMENT_DENIED)
    messageVM.arg.value = messageArg
    navigate(Main.message)
}

fun Main.Dialog<*>.onPaymentFailed(messageArg: MessageArg?) {
    progressVM.arg.postValue(null)
    paymentVM.arg.postValue(null)
    timeoutVM.startTimeout(Timeout.PAYMENT_DENIED)
    messageVM.arg.value = messageArg
    navigate(Main.message)
}

fun Main.Fragment<*>.onPaymentCancel() {
    timeoutVM.stopTimeout()
    paymentVM.arg.postValue(null)
    navigate(Main.adv) {
        setNoneAnim()
        setLaunchSingleTop()
    }
}

fun Main.Dialog<*>.onPaymentCancel() {
    timeoutVM.stopTimeout()
    paymentVM.arg.postValue(null)
    navigate(Main.adv) {
        setNoneAnim()
        setLaunchSingleTop()
    }
}

class Main {

    abstract class Fragment<T : BaseViewModel> : BaseFragment() {

        protected val localVM by lazy { activityVM(localViewModel()) }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            localVM.onStart()
            localVM.eventLiveData.observe {
                onLiveEventChanged(it)
            }
        }

        abstract fun localViewModel(): KClass<T>

        abstract fun onLiveEventChanged(event: Int)

    }

    abstract class Dialog<T : BaseViewModel> : BaseDialog() {

        protected val localVM by lazy { activityVM(localViewModel()) }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            localVM.onStart()
            localVM.eventLiveData.observe {
                onLiveEventChanged(it)
            }
        }

        abstract fun localViewModel(): KClass<T>

        abstract fun onLiveEventChanged(event: Int)
    }

    companion object {

        val mainDirection by lazy {
            EventLiveData<NavDirections>()
        }

        val pin = MainDirections.actionGlobalPinFragment()

        val adv = MainDirections.actionGlobalAdvFragment()

        val otp = MainDirections.actionGlobalOtpFragment()

        val card = MainDirections.actionGlobalCardFragment()

        val splash = MainDirections.actionGlobalSplashFragment()

        val face = MainDirections.actionGlobalFaceFragment()

        val payment = MainDirections.actionGlobalPaymentFragment()

        val message = MainDirections.actionGlobalMessageFragment()

        val confirm = MainDirections.actionGlobalConfirmFragment()

        val connect = MainDirections.actionGlobalConnectFragment()

        val device = MainDirections.actionGlobalDeviceFragment()

        val qr = MainDirections.actionGlobalQrFragment()

        val progress = MainDirections.actionGlobalProgressFragment()
    }

}


