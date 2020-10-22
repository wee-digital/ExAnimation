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

    var stepCount = 3

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

    fun build() {
        val set = ConstraintSet()
        set.clone(pinProgressLayout)
        val rangeWidth = pinProgressLayout.measuredWidth
        val stepWidth = rangeWidth / stepCount
        val indicatorWidth = pinProgressLayout.measuredHeight
        var stepX = 0
        while (stepX <= rangeWidth) {
            val view = View(context).also {
                it.id = View.generateViewId()
                it.setBackgroundResource(R.drawable.drw_pin_indicator)
                it.layoutParams = ConstraintLayout.LayoutParams(indicatorWidth, indicatorWidth)

            }
            pinProgressLayout.addView(view)
            set.connect(view.id, ConstraintSet.TOP, pinProgressLayout.id, ConstraintSet.TOP, stepX)
            stepX += stepWidth
        }


        // ... similarly add other constraints
        set.applyTo(pinProgressLayout);
    }
}