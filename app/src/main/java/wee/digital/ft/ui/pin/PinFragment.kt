package wee.digital.ft.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.ft.R
import wee.digital.ft.repository.dto.PaymentResponse
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.MainFragment
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.face.FaceFragment
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.progress.ProgressArg
import wee.digital.library.extension.encodedFromBase64
import wee.digital.library.extension.notNullOrEmpty
import wee.digital.library.extension.post
import wee.digital.library.extension.string

class PinFragment : MainDialog() {

    private val pinVM by lazy { viewModel(PinVM::class) }

    /**
     * [MainFragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onViewCreated() {
        addClickListener(pinViewClose)
        pinView.onViewInit()
        pinProgressLayout.onItemFilled = {
            onPinCodeFilled(it)
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.startTimeout(Timeout.PIN_VERIFY, MessageArg.timedOutError)
        pinVM.pinVerifySuccess.observe {
            onPinVerifySuccess(it)
        }
        pinVM.pinVerifyRetries.observe {
            onRestRetriesPinChanged(it)
        }
        pinVM.pinVerifyFailed.observe {
            sharedVM.startTimeout(it)
        }

        pinVM.payRequestSuccess.observe {
            onPayRequestSuccess(it)
        }
        pinVM.payRequestError.observe {
            onPayRequestError(it)
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
                dismissAllowingStateLoss()
                sharedVM.onPaymentCancel()
            }
        }
    }

    /**
     * [FaceFragment] properties
     */
    private fun onPinCodeFilled(pinCode: String) {
        sharedVM.showProgress(ProgressArg.pay)
        post(400) {
            pinVM.onPinVerify(
                    pinCode = pinCode,
                    paymentArg = sharedVM.payment.value,
                    faceArg = sharedVM.face.value
            )
        }
    }

    private fun onPinVerifySuccess(it: PinArg) {
        sharedVM.pin.value = it
        when {
            it.otpForm.notNullOrEmpty() -> {
                onOtpRequired(it.otpForm)
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

    private fun onRestRetriesPinChanged(restRetries: Int) {
        pinProgressLayout.clear()
        sharedVM.hideProgress()
        sharedVM.startTimeout(Timeout.PIN_VERIFY, MessageArg.timedOutError)
        pinView.onBindErrorText(string(R.string.pin_retry_msg).format(restRetries))
    }

    private fun onOtpRequired(otpForm: String) {
        dismissAllowingStateLoss()
        sharedVM.hideProgress()
        sharedVM.otpForm.value = otpForm.encodedFromBase64
        navigate(Main.otp)
    }

    private fun onFetchCardList() {
        pinVM.fetchCardList(sharedVM.pin.value?.userId)
        pinVM.cardList.observe {
            sharedVM.hideProgress()
            sharedVM.cardList.value = it
            dismissAllowingStateLoss()
            navigate(Main.card)
        }
    }

    private fun onPayRequestSuccess(it: PaymentResponse) {
        when {
            it.otpForm.notNullOrEmpty() -> {
                onOtpRequired(it.otpForm)
            }
            it.code == 0 -> {
                dismissAllowingStateLoss()
                sharedVM.progress.postValue(ProgressArg.paid)
            }
            else -> {
                onPayRequestError(MessageArg.fromCode(it.code))
            }
        }
    }

    private fun onPayRequestError(it: MessageArg) {
        dismissAllowingStateLoss()
        sharedVM.hideProgress()
        sharedVM.startTimeout(it)
    }


}