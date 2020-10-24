package wee.digital.fpa.ui.pin

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlinx.android.synthetic.main.pin_progress.view.*
import wee.digital.fpa.R
import wee.digital.library.extension.backgroundTint
import wee.digital.library.extension.tint
import wee.digital.library.widget.AppCustomView

class PinProgressView : AppCustomView {

    /**
     * [AppCustomView] override
     */
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun layoutResource(): Int {
        return R.layout.pin_progress
    }

    override fun onInitialize(context: Context, types: TypedArray) {
    }

    /**
     * [PinProgressView] properties
     */
    var itemCount = 3

    private val lastItemIndex: Int get() = itemCount - 1

    private val indicatorList = mutableListOf<View>()

    private val keyList = mutableListOf<Int>()

    private var indicatorWidth: Int = 0

    private var stepWidth: Int = 0

    var descentColor: Int = Color.DKGRAY

    var accentColor: Int = Color.BLUE

    fun build() {
        pinProgressLayout.post {
            pinProgressLayout.addIndicator()
        }
    }

    private fun ConstraintLayout.addIndicator() {
        indicatorWidth = this.measuredHeight
        val rangeWidth = this.measuredWidth - indicatorWidth
        stepWidth = rangeWidth / (itemCount - 1)
        var step = 0
        val set = ConstraintSet()
        set.clone(this)
        while (step <= itemCount) {
            addIndicator(set, step)
            step++
        }
    }

    private fun ConstraintLayout.addIndicator(set: ConstraintSet, step: Int) {
        val dotView = View(context).also {
            it.id = View.generateViewId()
            it.setBackgroundResource(R.drawable.drw_pin_indicator)
            it.backgroundTint(ContextCompat.getColor(context, R.color.colorGray))
            this.addView(it, this.childCount)
        }
        val dotId = dotView.id
        set.also {
            it.constrainDefaultWidth(dotId, measuredHeight)
            it.constrainWidth(dotId, measuredHeight)
            it.constrainDefaultHeight(dotId, measuredHeight)
            it.constrainHeight(dotId, measuredHeight)
            it.connect(dotId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            it.connect(dotId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, step * stepWidth)
            it.applyTo(this)
        }
        indicatorList.add(dotView)
        addRoundedView(set, step)
    }

    private fun ConstraintLayout.addRoundedView(set: ConstraintSet, step: Int) {
        if (step >= lastItemIndex - 1) {
            return
        }
        val alphaStep = 255 / lastItemIndex * (step + 1)
        val color = Color.argb(alphaStep, accentColor.red, accentColor.green, accentColor.blue)

        val roundedView = AppCompatImageView(context).also {
            it.id = View.generateViewId()
            it.setBackgroundResource(R.drawable.drw_pin_indicator)
            it.backgroundTint(Color.WHITE)
            it.scaleType = ImageView.ScaleType.FIT_XY
            it.tint(color)
            it.setImageResource(R.drawable.drw_pin_indicator)
            this.addView(it, this.childCount)
        }

        val roundId = roundedView.id
        val w = stepWidth + measuredHeight
        set.also {
            it.constrainDefaultWidth(roundId, w)
            it.constrainWidth(roundId, w)
            it.constrainDefaultHeight(roundId, measuredHeight / 2)
            it.constrainHeight(roundId, measuredHeight / 2)
            it.connect(roundId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            it.connect(roundId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            it.connect(roundId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, step * stepWidth)
            it.applyTo(this)
        }
    }

    fun incrementProgress(number: Int) {
        if (keyList.size == itemCount) return
        keyList.add(number)
        val index = keyList.lastIndex - 1
        TransitionManager.beginDelayedTransition(this)
        val set = ConstraintSet()
        val viewId = viewProgress.id
        set.clone(pinProgressLayout)
        if (index < 0) set.also {
            it.constrainDefaultWidth(viewId, measuredHeight)
            it.constrainWidth(viewId, measuredHeight)
            it.applyTo(pinProgressLayout)
            return
        }
        val w = stepWidth + measuredHeight
        val margin = keyList.lastIndex * stepWidth
        set.also {
            it.constrainDefaultWidth(viewId, w)
            it.constrainWidth(viewId, w)
            it.connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
            it.applyTo(pinProgressLayout)
        }
    }

    fun decrementProgress() {
        keyList.removeAt(keyList.lastIndex)
    }

}