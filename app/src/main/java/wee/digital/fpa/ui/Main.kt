package wee.digital.fpa.ui

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.vm.SharedVM


fun Fragment.onPaymentCancel() {
    activityVM(SharedVM::class).apply {
        progress.postValue(null)
        payment.postValue(null)
        stopTimeout()
    }
    val options = NavOptions.Builder().apply {
        setLaunchSingleTop(true)
        setPopUpTo(R.id.main, false)
    }
    findNavController().navigate(Main.adv, options.build())
}

fun Fragment.onPaymentFailed(messageArg: MessageArg?) {
    activityVM(SharedVM::class).apply {
        progress.postValue(null)
        payment.postValue(null)
        message.value = messageArg
    }
    val options = NavOptions.Builder().apply {
        setEnterAnim(R.anim.vertical_reserved_enter)
        setPopEnterAnim(R.anim.vertical_reserved_pop_enter)
    }
    findNavController().navigate(Main.message, options.build())
}

fun Fragment.onPaymentTimeout() {
    onPaymentFailed(MessageArg(
            title = "",
            message = "",
    ))
}

object Main {

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


