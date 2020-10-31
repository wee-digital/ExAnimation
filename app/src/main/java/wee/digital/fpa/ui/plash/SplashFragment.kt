package wee.digital.fpa.ui.plash

import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.fpa.ui.paymentVM
import wee.digital.fpa.ui.timeoutVM
import wee.digital.fpa.util.startCamera
import wee.digital.fpa.util.stopCamera
import kotlin.reflect.KClass

class SplashFragment : Main.Fragment<SplashVM>() {


    private val splashView by lazy { SplashView(this) }

    /**
     * [BaseFragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.splash
    }

    override fun localViewModel(): KClass<SplashVM> {
        return SplashVM::class
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

    override fun onLiveEventChanged(event: Int) {
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