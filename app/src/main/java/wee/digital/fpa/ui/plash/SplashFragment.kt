package wee.digital.fpa.ui.plash

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.MainVM
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.payment.PaymentArg

class SplashFragment : BaseFragment() {

    private val mainVM by lazy { activityVM(MainVM::class) }

    private val v by lazy { SplashView(this) }

    /**
     * [BaseFragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.splash
    }

    override fun onViewCreated() {
        v.onViewInit()
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
                v.stopPaymentRemaining()
                v.animateStopRemaining()
            }
            else -> {
                navigate(MainDirections.actionGlobalPaymentFragment())
                v.animateStartRemaining()
                v.startPaymentRemaining {
                    mainVM.paymentArg.value = null
                }
            }
        }
    }

}