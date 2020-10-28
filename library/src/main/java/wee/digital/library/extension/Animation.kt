package wee.digital.library.extension

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.Transition
import androidx.transition.TransitionManager

fun View.animRotateAxisY(block: ObjectAnimator.() -> Unit): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, "rotationY", 0.0f, 360f).also {
        it.interpolator = AccelerateDecelerateInterpolator()
        it.block()
    }
}

fun animFadeIn(duration: Long = 500): AlphaAnimation {
    val anim = AlphaAnimation(0f, 1f)
    anim.duration = duration
    anim.fillAfter = true
    return anim
}

fun animFadeOut(duration: Long = 500): AlphaAnimation {
    val anim = AlphaAnimation(1f, 0f)
    anim.duration = duration
    anim.fillAfter = true
    return anim
}

fun animCenterScale(duration: Long = 500): ScaleAnimation {
    return ScaleAnimation(
            0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    ).also {
        it.duration = duration
    }
}

fun Animation?.onAnimationStart(onStart: () -> Unit): Animation? {
    this?.setAnimationListener(object : SimpleAnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart()
        }
    })
    return this
}

fun Animation?.onAnimationEnd(onEnd: () -> Unit): Animation? {
    this?.setAnimationListener(object : SimpleAnimationListener {
        override fun onAnimationEnd(animation: Animation?) {
            onEnd()
        }
    })
    return this
}

fun ObjectAnimator.onAnimatorEnd(onEnd: () -> Unit): ObjectAnimator {
    this.addListener(object : SimpleAnimatorListener {
        override fun onAnimationEnd(animator: Animator?) {
            onEnd()
        }
    })
    return this
}

interface SimpleAnimationListener : Animation.AnimationListener {
    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
    }
}

interface SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animator: Animator?) {
    }

    override fun onAnimationEnd(animator: Animator?) {
    }

    override fun onAnimationCancel(animator: Animator?) {
    }

    override fun onAnimationStart(animator: Animator?) {
    }
}

fun Transition.beginTransition(layout: ConstraintLayout, vararg blocks: ConstraintSet.() -> Unit): Transition {
    if (blocks.isEmpty()) return this
    val set = ConstraintSet()
    set.clone(layout)
    for (i in 0 until blocks.lastIndex) {
        this.addListener(object : SimpleTransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                this@beginTransition.removeListener(this)
                TransitionManager.beginDelayedTransition(layout, this@beginTransition)
                blocks[i + 1](set)
                set.applyTo(layout)
            }
        })
    }
    TransitionManager.beginDelayedTransition(layout, this@beginTransition)
    blocks[0](set)
    set.applyTo(layout)
    return this
}

fun Transition.beginTransition(layout: ConstraintLayout, block: ConstraintSet.() -> Unit): Transition {
    TransitionManager.beginDelayedTransition(layout, this@beginTransition)
    val set = ConstraintSet()
    set.clone(layout)
    set.block()
    set.applyTo(layout)
    return this
}

fun Transition.onEndTransition(block: () -> Unit): Transition {
    addListener(object : SimpleTransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            this@onEndTransition.removeListener(this)
            block()
        }
    })
    return this
}

interface SimpleTransitionListener : Transition.TransitionListener {
    override fun onTransitionStart(transition: Transition) {
    }

    override fun onTransitionEnd(transition: Transition) {
    }

    override fun onTransitionCancel(transition: Transition) {
    }

    override fun onTransitionPause(transition: Transition) {
    }

    override fun onTransitionResume(transition: Transition) {
    }

}
