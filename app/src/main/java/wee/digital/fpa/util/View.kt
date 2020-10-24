package wee.digital.fpa.util

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import wee.digital.fpa.R

private var lasFastClickTime: Long = 0

private var clickCount: Int = 0

private fun resetFastClickCount() {
    lasFastClickTime = 0
    clickCount = 0
}

fun View.setFastClickListener(clickTimeCount: Int, block: (View?) -> Unit) {
    setOnClickListener {
        if (System.currentTimeMillis() - lasFastClickTime > 250 || clickCount >= clickTimeCount) {
            clickCount = 0
        }
        lasFastClickTime = System.currentTimeMillis()
        clickCount++
        if (clickCount == clickTimeCount) {
            resetFastClickCount()
            block(it)
        }
    }
}

fun AppCompatTextView.errorQR(value: String, block: () -> Unit) {
    block()
    this.text = value
    this.setTextColor(ContextCompat.getColor(context, R.color.colorAlert))
}

fun AppCompatTextView.resetErrorQR(value: String, block: () -> Unit) {
    block()
    this.text = value
    this.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
}