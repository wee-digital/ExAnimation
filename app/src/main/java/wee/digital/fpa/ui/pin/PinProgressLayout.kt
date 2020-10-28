package wee.digital.fpa.ui.pin

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
import wee.digital.fpa.R
import wee.digital.library.extension.*

class PinProgressLayout : ConstraintLayout {

    companion object {
        private val DEL = "DEL"
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    private var builder = Builder()

    private val lastItemIndex: Int get() = builder.itemCount - 1

    private val roundViewList = mutableListOf<View>()

    private val keyList = mutableListOf<String>()

    private val keyEvent = mutableListOf<String>()

    private var stepWidth: Int = 0

    private lateinit var progressView: View

    private val progressTransition = ChangeBounds().apply {
        duration = 200
    }

    private var isTransition = false

    private val extraKeyCount: Int
        get() {
            val addKey = keyEvent.filter { it != DEL }.size
            val delKey = keyEvent.filter { it == DEL }.size
            return keyList.size + addKey - delKey
        }

    var onItemFilled: (String) -> Unit = {}

    fun build(block: Builder.() -> Unit) {
        builder.block()
        build()
    }

    fun addKey(key: String) {
        if (extraKeyCount == builder.itemCount) return
        pushKey(key)
    }

    fun delKey() {
        if (extraKeyCount == 0) return
        pushKey(DEL)
    }

    private fun build() {
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
        progressTransition.addListener(object : SimpleTransitionListener {
            override fun onTransitionStart(transition: Transition) {
                isTransition = true
            }

            override fun onTransitionCancel(transition: Transition) {
                isTransition = false
            }

            override fun onTransitionEnd(transition: Transition) {

                isTransition = false
                if (keyEvent.isEmpty()) {
                    notifyInputChanged()
                }
                updateKeyEvent()
            }
        })
    }

    private fun addIndicator(step: Int) {
        val dotView = View(context).also {
            it.id = View.generateViewId()
            it.setBackgroundResource(R.drawable.bg_oval)
            it.backgroundTint(ContextCompat.getColor(context, R.color.colorGray))
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

    private fun updateKeyEvent() {
        if (keyEvent.isEmpty()) return
        when (val key = keyEvent[0]) {
            DEL -> {
                keyList.removeAt(keyList.lastIndex)
                transformProgressView(false)
            }
            else -> {
                keyList.add(key)
                transformProgressView(true)
            }
        }
        keyEvent.removeAt(0)

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

    fun notifyInputRemoved() {
        keyList.clear()
        val viewId = progressView.id
        progressTransition.beginTransition(this, {
            connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
            roundViewList.forEach {
                setVisibility(it.id,View.INVISIBLE)
            }
        },{
            connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        }, {
            constrainDefaultWidth(viewId, 1)
            constrainWidth(viewId, 1)
        }, {
            setVisibility(viewId, View.INVISIBLE)
        })
    }

    private fun pushKey(key: String) {
        keyEvent.add(key)
        if (!isTransition) {
            updateKeyEvent()
        }
    }

    class Builder {
        var itemCount = 3
        var progressColor: Int = Color.BLUE
    }
}