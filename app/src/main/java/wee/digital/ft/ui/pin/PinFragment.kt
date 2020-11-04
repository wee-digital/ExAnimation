package wee.digital.ft.ui.pin

import android.util.Base64
import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.ft.R
import wee.digital.ft.repository.dto.PaymentResponse
import wee.digital.ft.shared.Config
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.*
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.face.FaceFragment
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.progress.ProgressArg
import wee.digital.library.extension.post
import wee.digital.library.extension.string

class PinFragment : MainDialog() {

    private val pinVM by lazy { viewModel(PinVM::class) }

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
        if (Config.TESTING) post(2000) {
            pinVM.pinVerifySuccess.value = PinArg.testArg
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.startTimeout(Timeout.PIN_VERIFY)
        pinVM.pinVerifySuccess.observe {
            onPinVerifySuccess(it)
        }
        pinVM.pinVerifyFailed.observe {
            dismissAllowingStateLoss()
            onPaymentFailed(it)
        }
        pinVM.pinVerifyRetries.observe {
            onRestRetriesPinChanged(it)
        }
        pinVM.payRequestSuccess.observe {
            onPayRequestSuccess(it)
        }
        pinVM.payRequestError.observe {
            dismissAllowingStateLoss()
            onPaymentFailed(it)
        }
        pinVM.payRequestCardError.observe {
            onPaymentCardError()
        }
        pinVM.otpForm.observe {
            onOtpRequired(it)
        }
    }

    private fun onPaymentCardError() {
        sharedVM.cardError.postValue("Lỗi thanh toán. Bạn vui lòng chọn thẻ khác")
        onFetchCardList()
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
        post(1000) {
            pinVM.onPinVerify(
                    pinCode = pinCode,
                    paymentArg = sharedVM.payment.value,
                    faceArg = sharedVM.face.value
            )
        }
    }

    private fun onRestRetriesPinChanged(restRetries: Int) {
        sharedVM.progress.postValue(null)
        pinProgressLayout.clear()
        sharedVM.startTimeout(Timeout.PIN_VERIFY)
        pinView.onBindErrorText(string(R.string.pin_retry_msg).format(restRetries))
    }

    private fun onOtpRequired(otpForm: String) {
        dismissAllowingStateLoss()
        sharedVM.otpForm.value = otpForm
        navigate(Main.otp)
    }

    private fun onFetchCardList() {
        sharedVM.progress.postValue(ProgressArg.pay)
        pinVM.fetchCardList(sharedVM.pin.value?.userId)
        pinVM.cardList.observe {
            sharedVM.progress.value = null
            sharedVM.cardList.value = it
            dismissAllowingStateLoss()
            navigate(Main.card)
        }
    }

    private fun onPinVerifySuccess(it: PinArg) {
        sharedVM.pin.value = it
        when {
            it.haveOTP && !it.otpForm.isNullOrEmpty() -> {
                val s = String(Base64.decode(it.otpForm, Base64.NO_WRAP))
                pinVM.otpForm.postValue(s)
            }
            it.hasDefaultAccount -> {
                pinVM.onPayRequest(sharedVM.payment.value)
            }
            else -> {
                sharedVM.cardError.value = null
                onFetchCardList()
            }
        }
    }

    private fun onPayRequestSuccess(it: PaymentResponse) {
        when {
            it.haveOTP && !it.otpForm.isNullOrEmpty() -> {
                val s = String(Base64.decode(it.otpForm, Base64.NO_WRAP))
                pinVM.otpForm.postValue(s)
            }
            it.code == 0 -> {
                dismissAllowingStateLoss()
                sharedVM.progress.postValue(ProgressArg.paid)
            }
            else -> {
                pinVM.payRequestError.postValue(MessageArg.fromCode(it.code))
            }
        }
    }


}