package wee.digital.fpa.payment.pin

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.pin_progress_view.view.*
import wee.digital.fpa.R


class PinProgressView : FrameLayout {

    var stepCount = 6

    private var inputNum: MutableList<Int> = mutableListOf()

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        onViewInit(context, attrs)
    }

    private fun onViewInit(context: Context, attrs: AttributeSet?) {
        val types = context.theme.obtainStyledAttributes(attrs, R.styleable.PinProgress, 0, 0)
        try {
            LayoutInflater.from(context).inflate(R.layout.pin_progress_view, this)
            onConfigAttributes(context, types)
        } finally {
            types.recycle()
        }
    }

    private fun onConfigAttributes(context: Context, types: TypedArray) {

    }

    var stepX = 0
    fun build() {
        pinProgressLayout.post {
            pinProgressLayout.addIndicator()
        }

    }

    private fun ConstraintLayout.addIndicator() {
        val indicatorWidth = this.measuredHeight
        val rangeWidth = this.measuredWidth - indicatorWidth
        val stepWidth = rangeWidth / (stepCount - 1)

        if (stepX > rangeWidth) {
            return
        }

        val v = IndicatorView(context)
        v.id = View.generateViewId()
        this.addView(v, this.childCount)
        val set = ConstraintSet()
        set.clone(this)
        set.connect(v.id, ConstraintSet.START, this.id, ConstraintSet.START, stepX)
        set.connect(v.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
        set.applyTo(this)
        stepX += stepWidth

    }

    private fun onKeyEvent(){

    }
}