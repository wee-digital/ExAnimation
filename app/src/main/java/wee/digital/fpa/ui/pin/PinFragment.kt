package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.*
import wee.digital.fpa.ui.base.event
import wee.digital.fpa.ui.card.CardItem
import wee.digital.fpa.ui.card.CardVM
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.face.FaceFragment
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.progress.ProgressArg
import wee.digital.library.extension.string
import kotlin.reflect.KClass

class PinFragment : Main.Dialog<PinVM>() {

    private val pinView by lazy { PinView(this) }

    /**
     * [Main.Fragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun localViewModel(): KClass<PinVM> {
        return PinVM::class
    }

    override fun onViewCreated() {
        pinView.onViewInit()
        pinProgressLayout.onItemFilled = {
            onPinCodeFilled(it)
        }
    }

    override fun onLiveDataObserve() {
        localVM.restRetriesPin.observe {
            onRestRetriesPinChanged(it)
        }
        localVM.otpForm.observe {
            onOtpRequired(it)
        }
        timeoutVM.startTimeout(Timeout.PIN_VERIFY)
        timeoutVM.inTheEnd.observe {
            it ?: return@observe
            onPaymentCancel()
        }
        cardVM.cardList.event().observe {
            onCardListChanged(it)
        }
    }

    override fun onLiveEventChanged(event: Int) {
        when (event) {
            PinEvent.PIN_VERIFY_FAILED,
            PinEvent.PAY_REQUEST_FAILED -> {
                onPaymentFailed(MessageArg.paymentCancel)
            }
            PinEvent.PAY_REQUEST_SUCCESS -> {
                onPaymentSuccess()
            }
            PinEvent.PAY_REQUEST -> {
                localVM.onPayRequest(paymentVM.arg.value)
            }
            PinEvent.CARD_REQUIRED -> {
                onFetchCardList()
            }
            PinEvent.CARD_ERROR -> {
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
        timeoutVM.stopTimeout()
        progressVM.arg.postValue(ProgressArg.pay)
        pinVM.onPinVerify(
                pinCode = pinCode,
                paymentArg = paymentVM.arg.value,
                faceArg = faceVM.arg.value
        )
    }

    private fun onPaymentSuccess() {
        dismiss()
        progressVM.arg.postValue(ProgressArg.paid)
    }

    private fun onRestRetriesPinChanged(restRetries: Int) {
        progressVM.arg.postValue(null)
        pinProgressLayout.notifyInputRemoved()
        timeoutVM.startTimeout(Timeout.PIN_VERIFY)
        pinView.onBindErrorText(string(R.string.pin_retry_msg).format(restRetries))
    }

    private fun onOtpRequired(otpForm: String) {
        dismiss()
        otpVM.otpForm.value = otpForm
        navigate(Main.otp)
    }

    private fun onFetchCardList() {
        progressVM.arg.postValue(ProgressArg.pay)

        cardVM.fetchCardList(pinVM.arg.value)
    }

    private fun onCardListChanged(it: List<CardItem>?) {
        progressVM.arg.postValue(null)
        when {
            it.isNullOrEmpty() -> {
                onPaymentCancel()
            }
            else -> {
                dismiss()
                val s = "Lỗi thanh toán. Bạn vui lòng chọn thẻ khác"
                if (localVM.hasCardError){
                    cardVM.arg.postValue(s)
                }
                navigate(Main.card)
            }
        }
    }


}