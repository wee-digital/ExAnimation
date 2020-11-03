package wee.digital.ft.ui.pin.view

import android.content.Context
import android.graphics.Color
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
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import wee.digital.ft.R
import wee.digital.library.extension.*

class PinProgressLayout : ConstraintLayout {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    private var builder = Builder()

    private val lastItemIndex: Int get() = builder.itemCount - 1

    private val roundViewList = mutableListOf<View>()

    private val keyList = mutableListOf<String>()

    private var stepWidth: Int = 0

    private lateinit var progressView: View

    private val progressTransition = ChangeBounds().apply {
        duration = 100
    }

    var onItemFilled: (String) -> Unit = {}

    fun build(block: Builder.() -> Unit) {
        builder.block()
        this.post {
            val rangeWidth = this.measuredWidth - this.measuredHeight
            stepWidth = rangeWidth / (builder.itemCount - 1)
            var step = 0
            while (step <= builder.itemCount) {
                addIndicator(step)
                if (step < builder.itemCount - 1) {
                    addRoundedView(step)
                }
                step++
            }
            addProgressView()
        }
    }

    private fun addIndicator(step: Int) {
        val dotView = View(context).also {
            it.id = View.generateViewId()
            it.setBackgroundResource(R.drawable.bg_oval)
            it.backgroundTint(color(R.color.colorGray))
            addView(it, this.childCount)
        }
        val dotId = dotView.id
        ConstraintSet().also {
            it.clone(this)
            it.constrainDefaultWidth(dotId, measuredHeight)
            it.constrainWidth(dotId, measuredHeight)
            it.constrainDefaultHeight(dotId, measuredHeight)
            it.constrainHeight(dotId, measuredHeight)
            it.connect(dotId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            it.connect(dotId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, step * stepWidth)
            it.applyTo(this)
        }
    }

    private fun addRoundedView(step: Int) {
        val alphaStep = 255 / lastItemIndex * (step + 1)
        val baseColor = builder.progressColor
        val color = Color.argb(alphaStep, baseColor.red, baseColor.green, baseColor.blue)
        val roundedView = AppCompatImageView(context).also {
            it.id = View.generateViewId()
            it.setBackgroundResource(R.drawable.bg_rounded_full)
            it.backgroundTint(Color.WHITE)
            it.scaleType = ImageView.ScaleType.FIT_XY
            it.tint(color)
            it.setImageResource(R.drawable.bg_rounded_full)
            this.addView(it, this.childCount)
        }
        val roundId = roundedView.id
        val w = stepWidth + measuredHeight
        ConstraintSet().also {
            it.clone(this)
            it.constrainDefaultWidth(roundId, w)
            it.constrainWidth(roundId, w)
            it.constrainDefaultHeight(roundId, measuredHeight)
            it.constrainHeight(roundId, measuredHeight)
            it.setVisibility(roundId, View.INVISIBLE)
            it.connect(roundId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            it.connect(roundId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            it.connect(roundId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, step * stepWidth)
            it.applyTo(this)
        }
        roundViewList.add(roundedView)
    }

    private fun addProgressView() {
        progressView = View(context).also {
            it.id = View.generateViewId()
            it.setBackgroundResource(R.drawable.bg_rounded_full)
            it.backgroundTint(builder.progressColor)
            addView(it, this.childCount)
        }
        val viewId = progressView.id
        ConstraintSet().also {
            it.clone(this)
            it.setVisibility(viewId, View.INVISIBLE)
            it.constrainDefaultWidth(viewId, measuredHeight)
            it.constrainWidth(viewId, measuredHeight)
            it.constrainDefaultHeight(viewId, measuredHeight)
            it.constrainHeight(viewId, measuredHeight)
            it.connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            it.connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            it.applyTo(this)
        }
    }

    private fun transformProgressView(isForward: Boolean) {
        val index = keyList.lastIndex
        val viewId = progressView.id
        progressTransition.beginTransition(this) {
            if (index < 0) {
                setVisibility(viewId, View.INVISIBLE)
            } else {
                val w = when (index) {
                    0 -> measuredHeight
                    else -> stepWidth + measuredHeight
                }
                val margin = (index - 1) * stepWidth
                if (isForward) getRoundedViewId(index - 2)?.also { id ->
                    setVisibility(id, View.VISIBLE)
                } else getRoundedViewId(index)?.also { id ->
                    setVisibility(id, View.INVISIBLE)
                }
                setVisibility(viewId, View.VISIBLE)
                constrainDefaultWidth(viewId, w)
                constrainWidth(viewId, w)
                connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
            }
        }
    }

    private fun getRoundedViewId(index: Int): Int? {
        if (index !in 0..roundViewList.lastIndex) return null
        return roundViewList[index].id
    }

    private fun notifyInputChanged() {
        when (keyList.size) {
            builder.itemCount -> {
                val strings = StringBuilder()
                keyList.iterator().forEach { strings.append(it) }
                onItemFilled(strings.toString())
            }
        }
    }

    fun pushKey(key: Any?) {
        when (key) {
            R.drawable.drw_pin_del -> if (keyList.size > 0) {
                keyList.removeAt(keyList.lastIndex)
                notifyInputChanged()
                transformProgressView(false)
            }
            is String -> if (keyList.size < builder.itemCount) {
                keyList.add(key)
                notifyInputChanged()
                transformProgressView(true)
            }
        }
    }

    fun clear() {
        keyList.clear()
        this.clearAnimation()
        transformProgressView(false)
        val viewId = progressView.id
        progressTransition.beginTransition(this) {
            setVisibility(viewId, View.INVISIBLE)
            constrainDefaultWidth(viewId, measuredHeight)
            constrainWidth(viewId, measuredHeight)
            connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
            roundViewList.forEach {
                setVisibility(it.id, View.INVISIBLE)
            }
        }
    }

    class Builder {
        var itemCount = 3
        var progressColor: Int = Color.BLACK
    }

}