package wee.digital.fpa.payment.card

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
        paymentImageViewCard.flipAnimation()
    }

    private fun View.flipAnimation(){
        this.animate().rotationX(90f).setDuration(300).setListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation : Animator) {
                this@flipAnimation.rotationX = 90f;
                this@flipAnimation.animate().rotationX(0f).setDuration(300).setListener(null);
            }

        });
    }

}