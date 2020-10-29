package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.repository.dto.VerifyFaceDTOResp
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOResp
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.face.FaceFragment
import wee.digital.fpa.ui.face.FaceVM
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.message.MessageVM
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
        pinVM.retryMessage.observe {
            onRetryMessage(it)
        }
        pinVM.errorMessage.observe {
            onErrorMessage(it)
        }
        pinVM.pinCodeResponse.observe {
            onPinVerifySuccess(it)
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
    private val faceVM by lazy { activityVM(FaceVM::class) }

    private val paymentVM by lazy { activityVM(PaymentVM::class) }

    private val pinVM by lazy { viewModel(PinVM::class) }

    private val pinView by lazy { PinView(this) }

    private fun onPinCodeFilled(pinCode: String) {
        timeoutVM.stopTimeout()
        pinVM.onPinFilled(
                pinCode = pinCode,
                paymentArg = paymentVM.paymentArg.value,
                deviceInfo = mainVM.deviceInfo.value
        )
    }

    private fun onPinVerifySuccess(it: VerifyPINCodeDTOResp) {
        dismiss()
        when {
            it.hasDefaultAccount -> {
                // Nếu user có thẻ mặc định: chuyển pop-up Napas_form (Webview Napas) với thẻ mặc
                navigate(MainDirections.actionGlobalOtpFragment())
            }
            else -> {
                // Nếu user không có thẻ mặc định: chuyển pop-up Card_select (Chọn thẻ)
                navigate(MainDirections.actionGlobalCardFragment())
            }
        }
    }

    private fun onRetryMessage(it: String) {
        pinProgressLayout.notifyInputRemoved()
        timeoutVM.startTimeout(Timeout.PIN_VERIFY)
        pinView.onBindErrorText(it)
    }

    private fun onErrorMessage(it: MessageArg) {
        paymentVM.paymentArg.postValue(null)
        timeoutVM.startTimeout(Timeout.PAYMENT_DENIED)
        activityVM(MessageVM::class).arg.value = it
        navigate(MainDirections.actionGlobalMessageFragment())
    }

    private fun onPaymentDeny() {
        paymentVM.paymentArg.postValue(null)
        timeoutVM.stopTimeout()
        dismiss()
        navigate(MainDirections.actionGlobalSplashFragment()) {
            setLaunchSingleTop()
        }
    }

}