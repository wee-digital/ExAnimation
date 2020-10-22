package wee.digital.fpa.ui.card

import android.os.Bundle
import kotlinx.android.synthetic.main.card.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class CardFragment : BaseFragment() {

    private val adapter = CardAdapter()

    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun onCreated(state: Bundle?) {
        adapter.set(CardItem.defaultList)
        adapter.bind(paymentRecyclerViewCard, 2)
    }

}