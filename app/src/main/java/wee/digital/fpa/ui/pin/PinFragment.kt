package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main

class PinFragment : Main.Dialog() {

    private val pinVM by lazy { viewModel(PinVM::class) }

    private val pinView by lazy { PinView(this) }

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
        timeoutVM.startTimeout(Timeout.PIN_TIMEOUT)
        timeoutVM.inTheEnd.observe {
            if (it) onPaymentDeny()
        }
        pinVM.errorMessage.observe {
            pinProgressLayout.notifyInputRemoved()
            pinView.onBindErrorText(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            pinViewClose -> onPaymentDeny()
        }
    }

    private fun onPinCodeFilled(pinCode: String) {
        timeoutVM.startTimeout(Timeout.PIN_TIMEOUT)
        pinVM.onPinFilled(
                pinCode = pinCode,
                paymentArg = mainVM.paymentArg.value,
                deviceInfo = mainVM.deviceInfo.value
        )
    }

    private fun onPaymentDeny() {
        mainVM.paymentArg.postValue(null)
        timeoutVM.stopTimeout()
        dismiss()
        navigate(MainDirections.actionGlobalSplashFragment()) {
            setLaunchSingleTop()
        }

    }


}