package wee.digital.fpa.ui.plash

import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainFragment
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.onPaymentCancel
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.fpa.util.startCamera
import wee.digital.fpa.util.stopCamera

class SplashFragment : MainFragment() {

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
        sharedVM.timeoutColor.value = R.color.colorTimeoutSplash
        sharedVM.payment.observe {
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
                    onPaymentCancel()
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