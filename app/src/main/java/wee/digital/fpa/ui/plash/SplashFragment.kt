package wee.digital.fpa.ui.plash

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.fpa.ui.payment.PaymentVM
import wee.digital.fpa.util.startCamera
import wee.digital.fpa.util.stopCamera

class SplashFragment : Main.Fragment() {

    private val paymentVM by lazy { activityVM(PaymentVM::class) }

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
        timeoutVM.second.observe {
            splashView.onBindRemainingText(it)
        }
        paymentVM.arg.observe {
            onPaymentArgChanged(it)
        }
    }

    /**
     * [SplashFragment] properties
     */
    private fun onPaymentArgChanged(arg: PaymentArg?) {
        when (arg) {
            null -> {
                stopCamera()
                splashView.animateOnDismissPayment {
                    navigate(Main.adv) {
                        setNoneAnim()
                        setLaunchSingleTop()
                    }
                }
            }
            else -> {
                startCamera()
                navigate(Main.payment)
                splashView.animateOnHasPayment()
            }
        }
    }

}