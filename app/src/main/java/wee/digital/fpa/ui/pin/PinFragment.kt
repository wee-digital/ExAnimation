package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.repository.dto.PinArg
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.face.FaceFragment
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.progress.ProgressArg

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
            it ?: return@observe
            onPaymentDeny()
        }
        pinVM.onStart()
        pinVM.retryMessage.observe {
            onRetryMessage(it)
        }
        pinVM.errorMessage.observe {
            onErrorMessage(it)
        }
        pinVM.pinArg.observe {
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


    private val pinView by lazy { PinView(this) }

    private fun onPinCodeFilled(pinCode: String) {
        timeoutVM.stopTimeout()
        progressVM.arg.postValue(ProgressArg.payment)
        pinVM.onPinFilled(
                pinCode = pinCode,
                paymentArg = paymentVM.arg.value,
                faceArg = faceVM.faceArg.value
        )
    }

    private fun onPinVerifySuccess(it: PinArg?) {
        it ?: return
        dismiss()
        when {
            // Nếu user có thẻ mặc định: chuyển pop-up Napas_form (Webview Napas) với thẻ mặc
            it.hasDefaultAccount -> {
                navigate(Main.otp)
            }
            // Nếu user không có thẻ mặc định: chuyển pop-up Card_select (Chọn thẻ)
            else -> {
                navigate(Main.card)
            }
        }
    }

    private fun onRetryMessage(it: String) {
        progressVM.arg.postValue(null)
        view?.postDelayed({
            pinProgressLayout.notifyInputRemoved()
            timeoutVM.startTimeout(Timeout.PIN_VERIFY)
            pinView.onBindErrorText(it)
        }, 200)
    }

    private fun onErrorMessage(it: MessageArg) {
        progressVM.arg.postValue(null)
        paymentVM.arg.postValue(null)
        view?.postDelayed({
            timeoutVM.startTimeout(Timeout.PAYMENT_DENIED)
            messageVM.arg.value = it
            navigate(Main.message)
        }, 200)
    }

    private fun onPaymentDeny() {
        dismiss()
        timeoutVM.stopTimeout()
        paymentVM.arg.postValue(null)
        navigate(Main.adv) {
            setNoneAnim()
            setLaunchSingleTop()
        }
    }

}