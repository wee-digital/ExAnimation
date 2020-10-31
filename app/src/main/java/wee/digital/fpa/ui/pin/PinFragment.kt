package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.repository.dto.PaymentDTOResp
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.card.CardItem
import wee.digital.fpa.ui.confirm.ConfirmArg
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
            onPaymentCancel()
        }
        pinVM.onStart()
        pinVM.paymentSuccess.observe {
            onPaymentSuccess(it)
        }
        pinVM.pinVerifyError.observe {
            onPaymentError(MessageArg.paymentCancel)
        }
        pinVM.pinVerifyRetry.observe {
            onRetryMessage(it)
        }
        pinVM.cardRequired.observe {
            if (it) onCardRequired()
        }
        pinVM.cardError.observe {
            if (it) onCardError()
        }
        pinVM.otpRequired.observe {
            onOtpRequired(it)
        }
        cardVM.cardList.observe {
            onCardListChanged(it)
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
    private val pinView by lazy { PinView(this) }

    private fun onPinCodeFilled(pinCode: String) {
        timeoutVM.stopTimeout()
        progressVM.arg.postValue(ProgressArg.pay)
        pinVM.onPinFilled(
                pinCode = pinCode,
                paymentArg = paymentVM.arg.value,
                faceArg = faceVM.faceArg.value
        )

    }

    private fun onPaymentSuccess(it: PaymentDTOResp?) {
        it ?: return
        dismiss()
        progressVM.arg.postValue(ProgressArg.paid)
    }

    private fun onRetryMessage(it: Int) {
        progressVM.arg.postValue(null)
        pinProgressLayout.notifyInputRemoved()
        timeoutVM.startTimeout(Timeout.PIN_VERIFY)
        pinView.onBindErrorText("Mã PIN không đúng, bạn còn %s lần thử lại".format(it))
    }

    private fun onOtpRequired(it: PaymentDTOResp?) {
        it ?: return
        dismiss()
        otpVM.otpForm.value = it.formOtp
        navigate(Main.otp)
    }

    private fun onCardRequired() {
        cardVM.fetchCardList(pinVM.pinArg.value)
    }

    private fun onCardError() {
        progressVM.arg.postValue(null)
        val arg = ConfirmArg(
                headerGuideline = R.id.guidelineFace,
                title = "Giao dịch bị hủy bỏ",
                message = "Lỗi thanh toán. Bạn vui lòng chọn thẻ khác".format(),
                buttonAccept = "Đồng ý",
                onAccept = {
                    cardVM.fetchCardList(pinVM.pinArg.value)
                },
                buttonDeny = "Huỷ bỏ",
                onDeny = {
                    onPaymentCancel()
                },
        )
        confirmVM.arg.postValue(arg)
    }

    private fun onCardListChanged(it: List<CardItem>?) {
        progressVM.arg.postValue(null)
        it ?: return
        dismiss()
        navigate(Main.card)
    }


}