package wee.digital.ft.ui.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.ft.R
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.onPaymentFailed
import wee.digital.ft.ui.progress.ProgressArg

class CardFragment : MainDialog() {

    private val cardVM by lazy { viewModel(CardVM::class) }

    private val adapter = CardAdapter()

    /**
     * [MainDialog] override
     */
    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun onViewCreated() {
        adapter.bind(paymentRecyclerViewCard, 2)
        adapter.itemClick = { model, _ ->
            sharedVM.stopTimeout()
            cardVM.postPayRequest(model.accountId, sharedVM.payment.value)
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.startTimeout(Timeout.CARD_SELECT)
        sharedVM.timeoutEnd.observe {
            it ?: return@observe
            dismiss()
        }
        sharedVM.cardList.observe {
            adapter.set(it)
        }
        cardVM.otpFormLiveData.observe {
            onNavigateOTP(it)
        }
        cardVM.paymentSuccess.observe {
            onPaymentSuccess()
        }
        cardVM.paymentFailed.observe {
            onPaymentFailed(MessageArg.paymentCancel)
        }
    }

    /**
     * [CardFragment] properties
     */
    private fun onPaymentSuccess() {
        dismiss()
        sharedVM.progress.postValue(ProgressArg.paid)
    }

    private fun onNavigateOTP(otpForm: String) {
        dismiss()
        sharedVM.otpForm.value = otpForm
        navigate(Main.otp)
    }

}