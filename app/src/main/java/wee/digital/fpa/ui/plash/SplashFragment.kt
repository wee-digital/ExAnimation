package wee.digital.fpa.ui.plash

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.arg.PaymentArg
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.activityVM

class SplashFragment : Main.Fragment() {

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
        remainingVM.interval.observe {
            splashView.onBindRemainingText(it)
        }
    }

    /**
     * [SplashFragment] properties
     */
    private fun onPaymentArgChanged(arg: PaymentArg?) {
        when (arg) {
            null -> {
                splashView.animateOnDimissPayment()
            }
            else -> {
                navigate(MainDirections.actionGlobalPaymentFragment())
                splashView.animateOnHasPayment()
            }
        }
    }

}