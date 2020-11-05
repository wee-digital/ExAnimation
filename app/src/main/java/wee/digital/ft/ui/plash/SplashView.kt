package wee.digital.ft.ui.plash

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import kotlinx.android.synthetic.main.splash.view.*
import wee.digital.library.extension.beginTransition

class SplashView : ConstraintLayout {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    private val viewTransition = ChangeBounds().apply {
        duration = 400
    }

    fun animateOnHasPayment() {
        splashImageViewLogo?.post {
            try {
                val height = splashImageViewLogo.height / 2
                if (splashImageViewLogo.height == height) return@post
                val logoId = splashImageViewLogo.id
                viewTransition.beginTransition(this) {
                    constrainHeight(logoId, height)
                    setVerticalBias(logoId, 0.72f)
                    connect(logoId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                    connect(logoId, ConstraintSet.BOTTOM, guidelineSplash.id, ConstraintSet.BOTTOM)
                }
            } catch (ignore: Exception) {
            }
        }
    }

    fun animateOnDismissPayment(onEndTransition: () -> Unit) {
        splashImageViewLogo?.post {
            try {
                val height = splashImageViewLogo.height * 2
                if (splashImageViewLogo.height == height) return@post
                val logoId = splashImageViewLogo.id
                viewTransition.beginTransition(this, {
                    constrainHeight(logoId, height)
                    setVerticalBias(logoId, 0.5f)
                    connect(logoId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                    connect(logoId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                }, {
                    onEndTransition()
                })
            } catch (ignore: Exception) {
            }
        }
    }

    fun onViewInit() {
    }

}