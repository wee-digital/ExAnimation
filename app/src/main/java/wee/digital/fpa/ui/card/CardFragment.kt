package wee.digital.fpa.ui.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.fpa.R
import wee.digital.fpa.repository.dto.PaymentDTOResp
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.progress.ProgressArg

class CardFragment : Main.Dialog() {

    private val adapter = CardAdapter()

    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun onViewCreated() {
        adapter.bind(paymentRecyclerViewCard, 2)
        adapter.itemClick = { model, _ ->
            cardVM.postPayRequest(model.bankCode, paymentVM.arg.value)
        }
    }

    override fun onLiveDataObserve() {
        cardVM.cardList.observe {
            adapter.set(it)
        }
        cardVM.paymentSuccess.observe {
            onPaymentSuccess(it)
        }
        cardVM.paymentError.observe {
            if (it) onPaymentError(MessageArg.paymentCancel)
        }
        pinVM.otpRequired.observe {
            onOtpRequired(it)
        }
    }

    private fun onPaymentSuccess(it: PaymentDTOResp?) {
        it ?: return
        dismiss()
        progressVM.arg.postValue(ProgressArg.paid)
    }

    private fun onOtpRequired(it: PaymentDTOResp?) {
        it ?: return
        dismiss()
        otpVM.otpForm.value = it.formOtp
        navigate(Main.otp)
    }

}