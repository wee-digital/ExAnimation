package wee.digital.ft.ui.card

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import kotlinx.android.synthetic.main.card_item.view.*
import wee.digital.ft.R
import wee.digital.ft.repository.utils.SystemUrl
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.library.extension.load


class CardAdapter : BaseRecyclerAdapter<CardItem>() {

    override fun layoutResource(model: CardItem, position: Int): Int {
        return R.layout.card_item
    }

    override fun View.onBindModel(model: CardItem, position: Int, layout: Int) {
        paymentViewCard.flipAnimation()
        onBindBackground(model)
        cardItemLogo.load("%s%s.png".format(SystemUrl.URL_BANK_LOGO_WHITE, model.bankCode))
        cardItemIcon.load("%s%s.png".format(SystemUrl.URL_BANK_IC_WHITE, model.bankCode))
    }

    private fun View.onBindBackground(model: CardItem) {
        val colors = intArrayOf(Color.parseColor(model.colors[0]), Color.parseColor(model.colors[1]))
        val gd = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
        gd.cornerRadius = 15f
        paymentImageViewCard.background = gd
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