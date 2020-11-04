package wee.digital.ft.ui.plash

import kotlinx.android.synthetic.main.splash.*
import wee.digital.ft.R
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainFragment
import wee.digital.ft.ui.base.BaseFragment
import wee.digital.ft.ui.onPaymentCancel
import wee.digital.ft.ui.payment.PaymentArg
import wee.digital.ft.util.startCamera
import wee.digital.ft.util.stopCamera

class SplashFragment : MainFragment() {

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
                navigate(Main.payment)
                splashView.animateOnHasPayment()
            }
        }
    }

}