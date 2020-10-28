package wee.digital.fpa.ui.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseFragment

class CardFragment : Main.Fragment() {

    private val adapter = CardAdapter()

    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun onViewCreated() {
        adapter.set(CardItem.defaultList)
        adapter.bind(paymentRecyclerViewCard, 2)
    }

    override fun onLiveDataObserve() {
    }

}