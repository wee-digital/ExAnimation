package wee.digital.fpa.ui.plash

import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import kotlinx.android.synthetic.main.splash.*
import wee.digital.library.extension.beginTransition
import wee.digital.library.extension.bold
import wee.digital.library.extension.onEndTransition
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

    fun animateOnHasPayment(onAnimEnd: () -> Unit = {}) {
        val height = v.splashImageViewLogo.height / 2
        if (v.splashImageViewLogo.height == height) return
        viewTransition.onEndTransition {
            onAnimEnd()
        }
        val logoId = v.splashImageViewLogo.id
        viewTransition.beginTransition(v.viewContent) {
            constrainHeight(logoId, height)
            connect(logoId, ConstraintSet.TOP, v.splashTextViewRemaining.id, ConstraintSet.BOTTOM)
            connect(logoId, ConstraintSet.BOTTOM, v.splashGuideline.id, ConstraintSet.BOTTOM)
            setVerticalBias(logoId, 0.55f)
        }
    }

    fun animateOnDimissPayment() {
        val height = v.splashImageViewLogo.height * 2
        if (v.splashImageViewLogo.height == height) return
        val logoId = v.splashImageViewLogo.id
        viewTransition.beginTransition(v.viewContent) {
            constrainHeight(logoId, height)
            connect(logoId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(logoId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            setVerticalBias(logoId, 0.5f)
        }
    }


    fun onViewInit() {

    }


}