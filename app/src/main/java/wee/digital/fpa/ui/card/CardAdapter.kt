package wee.digital.fpa.ui.card

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import kotlinx.android.synthetic.main.card_item.view.*
import wee.digital.fpa.R
import wee.digital.fpa.repository.utils.SystemUrl
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.library.extension.load


class CardAdapter : BaseRecyclerAdapter<CardItem>() {

    override fun layoutResource(model: CardItem, position: Int): Int {
        return R.layout.card_item
    }

    override fun View.onBindModel(model: CardItem, position: Int, layout: Int) {
        paymentViewCard.flipAnimation()
        val colors = intArrayOf(Color.parseColor(model.colors[0]), Color.parseColor(model.colors[1]))
        val gd = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, colors)
        gd.cornerRadius = 15f
        paymentImageViewCard.background = gd
        val logo1 = "${SystemUrl.URL_BANK_LOGO_WHITE}${model.bankCode}.png"
        cardItemLogo.load(logo1)
        val logo2 = "${SystemUrl.URL_BANK_IC_WHITE}${model.bankCode}.png"
        cardItemIcon.load(logo2)
    }

    private fun View.flipAnimation() {
        this.animate().rotationX(-90f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@flipAnimation.rotationX = 90f
                this@flipAnimation.animate().rotationX(0f).setDuration(300).setListener(null)
            }
        })
    }

}