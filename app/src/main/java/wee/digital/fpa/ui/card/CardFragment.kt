package wee.digital.fpa.ui.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.base.viewModel
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.onPaymentFailed
import wee.digital.fpa.ui.progress.ProgressArg

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
            cardVM.postPayRequest(model.bankCode, sharedVM.payment.value)
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.cardList.observe {
            sharedVM.cardList.value = it
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