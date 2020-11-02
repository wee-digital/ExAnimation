package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.*
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.face.FaceFragment
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.progress.ProgressArg
import wee.digital.library.extension.string

class PinFragment : MainDialog() {

    private val pinVM by lazy { activityVM(PinVM::class) }

    private val pinView by lazy { PinView(this) }

    /**
     * [MainFragment] override
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
        pinVM.pinVerifySuccess.observe {
            onPinVerifySuccess(it)
        }
        pinVM.pinVerifyFailed.observe {
            onPaymentFailed(MessageArg.paymentCancel)
        }
        pinVM.pinVerifyRetries.observe {
            onRestRetriesPinChanged(it)
        }

        pinVM.payRequestSuccess.observe {
            dismiss()
            sharedVM.progress.postValue(ProgressArg.paid)
        }
        pinVM.payRequestError.observe {
            onPaymentFailed(MessageArg.paymentCancel)
        }

        pinVM.payRequestCardError.observe {
            onPaymentCardError()
        }
        pinVM.otpForm.observe {
            onOtpRequired(it)
        }

        sharedVM.startTimeout(Timeout.PIN_VERIFY)
        sharedVM.timeoutEnd.observe {
            it ?: return@observe
            onPaymentCancel()
        }

    }

    private fun onPaymentCardError() {
        sharedVM.cardError.postValue("Lỗi thanh toán. Bạn vui lòng chọn thẻ khác")
        onFetchCardList()
    }

    private fun onPinVerifySuccess(it: PinArg) {
        when {
            it.hasDefaultAccount -> {
                pinVM.onPayRequest(sharedVM.payment.value)
            }
            else -> {
                sharedVM.cardError.value = null
                onFetchCardList()
            }
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            pinViewClose -> {
                onPaymentCancel()
            }
        }
    }

    /**
     * [FaceFragment] properties
     */
    private fun onPinCodeFilled(pinCode: String) {
        sharedVM.stopTimeout()
        sharedVM.progress.postValue(ProgressArg.pay)
        pinVM.onPinVerify(
                pinCode = pinCode,
                paymentArg = sharedVM.payment.value,
                faceArg = sharedVM.face.value
        )
    }

    private fun onRestRetriesPinChanged(restRetries: Int) {
        sharedVM.progress.postValue(null)
        pinProgressLayout.notifyInputRemoved()
        sharedVM.startTimeout(Timeout.PIN_VERIFY)
        pinView.onBindErrorText(string(R.string.pin_retry_msg).format(restRetries))
    }

    private fun onOtpRequired(otpForm: String) {
        dismiss()
        sharedVM.otpForm.value = otpForm
        navigate(Main.otp)
    }

    private fun onFetchCardList() {
        sharedVM.progress.postValue(ProgressArg.pay)
        pinVM.fetchCardList(sharedVM.pin.value?.userId)
        pinVM.cardList.observe {
            sharedVM.progress.value = null
            sharedVM.cardList.value = it
            dismiss()
            navigate(Main.card)
        }
    }


}