package wee.digital.fpa.ui.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM

class CardFragment : Main.Dialog() {

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
        cardVM.cardList.observe {
            adapter.set(it)
        }
    }

}