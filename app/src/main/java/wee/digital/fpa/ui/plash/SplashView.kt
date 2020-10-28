package wee.digital.fpa.ui.plash

import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.splash.*
import wee.digital.fpa.util.SimpleTransitionListener
import wee.digital.library.extension.bold
import wee.digital.library.extension.setHyperText

class SplashView(private val v: SplashFragment) {

    private val viewTransition = ChangeBounds().apply {
        duration = 400
    }

    fun onBindRemainingText(second: Int) {
        if (second > 0) {
            val sHour = "%02d:%02d".format(second / 60, second % 60).bold()
            val sRemaining = "Thời gian còn lại: %s".format(sHour)
            v.splashTextViewRemaining.setHyperText(sRemaining)
        } else {
            v.splashTextViewRemaining.text = null
        }
    }

    fun animateStartRemaining(onAnimEnd: () -> Unit = {}) {
        viewTransition.addListener(object : SimpleTransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                onAnimEnd()
                viewTransition.removeListener(this)
            }
        })
        val logoId = v.splashImageViewLogo.id
        onViewAnimate {
            constrainHeight(logoId, v.splashImageViewLogo.height / 2)
            connect(logoId, ConstraintSet.TOP, v.splashTextViewRemaining.id, ConstraintSet.BOTTOM)
            connect(logoId, ConstraintSet.BOTTOM, v.splashGuideline.id, ConstraintSet.BOTTOM)
            setVerticalBias(logoId, 0.55f)
        }
    }

    fun animateStopRemaining() {
        val logoId = v.splashImageViewLogo.id
        onViewAnimate {
            constrainHeight(logoId, v.splashImageViewLogo.height * 2)
            connect(logoId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(logoId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            setVerticalBias(logoId, 0.5f)
        }
    }

    private fun onViewAnimate(block: ConstraintSet.() -> Unit) {
        TransitionManager.beginDelayedTransition(v.viewContent, viewTransition)
        ConstraintSet().also {
            it.clone(v.viewContent)
            it.block()
            it.applyTo(v.viewContent)
        }
    }

    fun onViewInit() {

    }


}