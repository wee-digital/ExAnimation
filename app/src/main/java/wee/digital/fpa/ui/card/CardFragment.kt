package wee.digital.fpa.ui.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.*
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.progress.ProgressArg
import kotlin.reflect.KClass

class CardFragment : Main.Dialog<CardVM>() {

    private val adapter = CardAdapter()

    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun localViewModel(): KClass<CardVM> {
        return CardVM::class
    }

    override fun onViewCreated() {
        adapter.bind(paymentRecyclerViewCard, 2)
        adapter.itemClick = { model, _ ->
            localVM.postPayRequest(model.bankCode, paymentVM.arg.value)
        }
    }

    override fun onLiveDataObserve() {
        localVM.cardList.observe {
            adapter.set(it)
        }
        localVM.otpForm.observe {
            onNavigateOTP(it)
        }
    }

    override fun onLiveEventChanged(event: Int) {
        when (event) {
            CardEvent.PAY_SUCCESS -> {
                onPaymentSuccess()
            }
            CardEvent.PAY_FAILED -> {
                onPaymentFailed(MessageArg.paymentCancel)
            }
        }
    }

    private fun onPaymentSuccess() {
        dismiss()
        progressVM.arg.postValue(ProgressArg.paid)
    }

    private fun onNavigateOTP(otpForm: String) {
        dismiss()
        otpVM.otpForm.value = otpForm
        navigate(Main.otp)
    }

}