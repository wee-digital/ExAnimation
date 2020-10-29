package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.face.FaceFragment
import wee.digital.fpa.ui.payment.PaymentVM

class PinFragment : Main.Dialog() {

    /**
     * [Main.Fragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onViewCreated() {
        pinView.onViewInit()
        pinProgressLayout.onItemFilled = {
            onPinCodeFilled(it)
        }
    }

    override fun onLiveDataObserve() {
        timeoutVM.startTimeout(Timeout.PIN_VERIFY)
        timeoutVM.inTheEnd.observe {
            if (it) onPaymentDeny()
        }
        pinVM.errorMessage.observe {
            pinProgressLayout.notifyInputRemoved()
            pinView.onBindErrorText(it)
        }
        pinVM.pinCodeSuccess.observe {
            onPinVerifySuccess()
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            pinViewClose -> onPaymentDeny()
        }
    }

    /**
     * [FaceFragment] properties
     */
    private val paymentVM by lazy { activityVM(PaymentVM::class) }

    private val pinVM by lazy { viewModel(PinVM::class) }

    private val pinView by lazy { PinView(this) }

    private fun onPinCodeFilled(pinCode: String) {
        timeoutVM.startTimeout(Timeout.PIN_VERIFY)
        pinVM.onPinFilled(
                pinCode = pinCode,
                paymentArg = paymentVM.paymentArg.value,
                deviceInfo = mainVM.deviceInfo.value
        )
    }

    private fun onPinVerifySuccess() {

    }

    private fun onPaymentDeny() {
        paymentVM.paymentArg.postValue(null)
        dismiss()
        navigate(MainDirections.actionGlobalSplashFragment()) {
            setLaunchSingleTop()
        }
    }


}