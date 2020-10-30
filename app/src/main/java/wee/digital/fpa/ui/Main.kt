package wee.digital.fpa.ui

import androidx.navigation.NavDirections
import wee.digital.fpa.MainDirections
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.confirm.ConfirmVM
import wee.digital.fpa.ui.connect.ConnectVM
import wee.digital.fpa.ui.face.FaceVM
import wee.digital.fpa.ui.message.MessageVM
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

        val payment = MainDirections.actionGlobalFaceFragment()

        val message = MainDirections.actionGlobalMessageFragment()

        val confirm = MainDirections.actionGlobalConfirmFragment()

        val connect = MainDirections.actionGlobalConnectFragment()

        val device = MainDirections.actionGlobalDeviceFragment()

        val qr = MainDirections.actionGlobalQrFragment()

        val progress = MainDirections.actionGlobalProgressFragment()

        val progressPay = MainDirections.actionGlobalProgressPayFragment()
    }


}