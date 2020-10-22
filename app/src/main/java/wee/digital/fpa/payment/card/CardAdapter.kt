package wee.digital.fpa.payment.card

import android.view.View
import kotlinx.android.synthetic.main.card_item.view.*
import wee.digital.fpa.R
import wee.digital.fpa.payment.base.BaseRecyclerAdapter

class CardAdapter : BaseRecyclerAdapter<CardItem>() {

    override fun layoutResource(model: CardItem, position: Int): Int {
        return R.layout.card_item
    }

    override fun View.onBindModel(model: CardItem, position: Int, layout: Int) {
        paymentImageViewCard.setImageResource(model.image)
    }

}