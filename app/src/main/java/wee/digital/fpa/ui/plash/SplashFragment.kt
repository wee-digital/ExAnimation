package wee.digital.fpa.ui.plash

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainVM
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.payment.PaymentArg

class SplashFragment : Main.Fragment() {

    private val mainVM by lazy { activityVM(MainVM::class) }

    private val splashVM by lazy { activityVM(SplashVM::class) }

    private val splashView by lazy { SplashView(this) }

    /**
     * [BaseFragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.splash
    }

    override fun onViewCreated() {
        splashView.onViewInit()
    }

    override fun onLiveDataObserve() {
        mainVM.paymentArg.observe {
            onPaymentArgChanged(it)
        }
    }

    /**
     * [SplashFragment] properties
     */
    private fun onPaymentArgChanged(arg: PaymentArg?) {
        when (arg) {
            null -> {
                splashView.stopPaymentRemaining()
                splashView.animateStopRemaining()
            }
            else -> {
                navigate(MainDirections.actionGlobalPaymentFragment())
                splashView.animateStartRemaining()
                splashView.startRemaining {
                    mainVM.paymentArg.value = null
                }
            }
        }
    }

}