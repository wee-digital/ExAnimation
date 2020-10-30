package wee.digital.fpa.ui.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.pin.PinVM

class CardFragment : Main.Dialog() {

    private val pinVM by lazy { activityVM(PinVM::class) }

    private val cardVM by lazy { viewModel(CardVM::class) }

    private val adapter = CardAdapter()

    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun onViewCreated() {
        adapter.bind(paymentRecyclerViewCard, 2)
        adapter.itemClick = { model, position ->
            dismiss()
            navigate(Main.otp)
        }
    }

    override fun onLiveDataObserve() {
        cardVM.fetchCardList(pinVM.pinArg.value)
        cardVM.cardList.observe {
            adapter.set(it)
        }
    }

}