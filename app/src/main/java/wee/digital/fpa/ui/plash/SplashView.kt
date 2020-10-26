package wee.digital.fpa.ui.plash

import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.splash.*
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.fpa.util.SimpleTransitionListener
import wee.digital.library.extension.bold
import wee.digital.library.extension.setHyperText
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SplashView(private val v: SplashFragment) {

    private val paymentInterval: Int = 10 // second

    private val viewTransition = ChangeBounds().apply {
        duration = 400
    }

    private var disposable: Disposable? = null

    private fun onBindRemainingText(second: Int) {
        val sHour = "%02d:%02d".format(second / 60, second % 60).bold()
        val sRemaining = "Thời gian còn lại: %s".format(sHour)
        v.splashTextViewRemaining.setHyperText(sRemaining)
    }

    fun animateStartRemaining(onAnimEnd: () -> Unit = {}) {
        onBindRemainingText(paymentInterval)
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
        v.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onDestroy() {
                disposable?.dispose()
            }
        })
    }

    fun startPaymentRemaining(onRemainingEnd: () -> Unit) {
        val waitingCounter = AtomicInteger(paymentInterval)
        disposable?.dispose()
        disposable = Observable
                .interval(1, 1, TimeUnit.SECONDS)
                .map {
                    waitingCounter.decrementAndGet()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it > 0) {
                        onBindRemainingText(it)
                    } else {
                        disposable?.dispose()
                        onRemainingEnd()
                    }
                }, {})

    }

    fun stopPaymentRemaining() {
        disposable?.dispose()
        v.splashTextViewRemaining.text = null
    }

}