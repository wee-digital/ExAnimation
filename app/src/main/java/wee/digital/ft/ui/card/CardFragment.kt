package wee.digital.ft.ui.card

import android.view.View
import kotlinx.android.synthetic.main.card.*
import wee.digital.ft.R
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.base.viewModel
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
        addClickListener(cardViewClose)
        adapter.bind(paymentRecyclerViewCard, 2)
        adapter.itemClick = { model, _ ->
            sharedVM.stopTimeout()
            cardVM.postPayRequest(model.accountId, sharedVM.payment.value)
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.startTimeout(Timeout.CARD_SELECT)
        sharedVM.cardList.observe {
            if (it != null) {
                adapter.set(it)
            } else {
                dismissAllowingStateLoss()
            }
        }
        cardVM.otpFormLiveData.observe {
            onNavigateOTP(it)
        }
        cardVM.paymentSuccess.observe {
            onPaymentSuccess()
        }
        cardVM.paymentFailed.observe {
            dismissAllowingStateLoss()
            sharedVM.startTimeout(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            cardViewClose -> {
                dismissAllowingStateLoss()
                sharedVM.onPaymentCancel()
            }
        }
    }

    /**
     * [CardFragment] properties
     */
    private fun onPaymentSuccess() {
        dismissAllowingStateLoss()
        sharedVM.showProgress(ProgressArg.paid)
    }

    private fun onNavigateOTP(otpForm: String) {
        dismissAllowingStateLoss()
        sharedVM.otpForm.value = otpForm
        navigate(Main.otp)
    }

}