package wee.digital.fpa.ui

import androidx.navigation.NavDirections
import wee.digital.fpa.MainDirections
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.base.activityVM
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

class Main {

    abstract class Fragment : BaseFragment() {

        val messageVM by lazy { activityVM(MessageVM::class) }

        val confirmVM by lazy { activityVM(ConfirmVM::class) }

        val timeoutVM by lazy { activityVM(TimeoutVM::class) }

        val paymentVM by lazy { activityVM(PaymentVM::class) }

        val faceVM by lazy { activityVM(FaceVM::class) }

        val progressVM by lazy { activityVM(ProgressVM::class) }

        fun onPaymentError(messageArg: MessageArg?) {
            progressVM.arg.postValue(null)
            paymentVM.arg.postValue(null)
            timeoutVM.startTimeout(Timeout.PAYMENT_DENIED)
            messageVM.arg.value = messageArg
            navigate(message)
        }

        fun onPaymentCancel() {
            timeoutVM.stopTimeout()
            paymentVM.arg.postValue(null)
            navigate(adv) {
                setNoneAnim()
                setLaunchSingleTop()
            }
        }

    }

    abstract class Dialog : BaseDialog() {

        val messageVM by lazy { activityVM(MessageVM::class) }

        val confirmVM by lazy { activityVM(ConfirmVM::class) }

        val progressVM by lazy { activityVM(ProgressVM::class) }

        val mainVM by lazy { activityVM(MainVM::class) }

        val timeoutVM by lazy { activityVM(TimeoutVM::class) }

        val connectVM by lazy { activityVM(ConnectVM::class) }

        val paymentVM by lazy { activityVM(PaymentVM::class) }

        val faceVM by lazy { activityVM(FaceVM::class) }

        val pinVM by lazy { activityVM(PinVM::class) }

        val cardVM by lazy { activityVM(CardVM::class) }

        val otpVM by lazy { activityVM(OtpVM::class) }

        fun onPaymentError(messageArg: MessageArg?) {
            progressVM.arg.postValue(null)
            paymentVM.arg.postValue(null)
            timeoutVM.startTimeout(Timeout.PAYMENT_DENIED)
            dismiss()
            messageVM.arg.value = messageArg
            navigate(message)
        }

        fun onPaymentCancel() {
            timeoutVM.stopTimeout()
            paymentVM.arg.postValue(null)
            dismiss()
            navigate(adv) {
                setNoneAnim()
                setLaunchSingleTop()
            }
        }
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