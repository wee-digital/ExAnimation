package wee.digital.fpa.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.button_view.view.*
import wee.digital.fpa.R
import wee.digital.library.extension.hide
import wee.digital.library.extension.load
import wee.digital.library.extension.show

class ButtonView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initView(context, attributeSet)
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.button_view, this)

        val types =
                context.theme.obtainStyledAttributes(attributeSet, R.styleable.ButtonView, 0, 0)

        val text = types.getString(R.styleable.ButtonView_android_text) ?: ""
        buttonViewLabel.text = text

        buttonViewLoading.load(R.drawable.loading)

    }

    fun showLoading() {
        val animHide = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animHide.fillAfter = true
        buttonViewLabel.startAnimation(animHide)
        buttonViewLoading.show()
    }

    fun hideLoading() {
        val animShow = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        animShow.fillAfter = true
        buttonViewLabel.startAnimation(animShow)
        buttonViewLoading.hide()
    }

}