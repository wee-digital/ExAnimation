package wee.digital.ft.ui.plash

import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import kotlinx.android.synthetic.main.splash.*
import wee.digital.ft.util.startCamera
import wee.digital.library.extension.beginTransition
import wee.digital.library.extension.onEndTransition

class SplashView(private val v: SplashFragment) {

    private val viewTransition = ChangeBounds().apply {
        duration = 400
    }

    fun animateOnHasPayment() {
        v.splashImageViewLogo?.post {
            try {
                val height = v.splashImageViewLogo.height / 2
                if (v.splashImageViewLogo.height == height) return@post
                val logoId = v.splashImageViewLogo.id
                viewTransition.beginTransition(v.slashViewContent) {
                    constrainHeight(logoId, height)
                    setVerticalBias(logoId, 0.72f)
                    connect(logoId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                    connect(logoId, ConstraintSet.BOTTOM, v.guidelineSplash.id, ConstraintSet.BOTTOM)
                    connect(logoId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                    connect(logoId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

                }
            } catch (ignore: Exception) {
            }
        }
    }

    fun animateOnDismissPayment(onEndTransition: () -> Unit) {
        v.splashImageViewLogo?.post {
            try {
                val height = v.splashImageViewLogo.height * 2
                if (v.splashImageViewLogo.height == height) return@post
                val logoId = v.splashImageViewLogo.id
                viewTransition.onEndTransition {
                    onEndTransition()
                }
                viewTransition.beginTransition(v.slashViewContent) {
                    constrainHeight(logoId, height)
                    setVerticalBias(logoId, 0.5f)
                    connect(logoId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                    connect(logoId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                    connect(logoId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                    connect(logoId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                }
            } catch (ignore: Exception) {
            }
        }
    }

    fun onViewInit() {
    }


}