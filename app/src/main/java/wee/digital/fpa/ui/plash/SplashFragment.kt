package wee.digital.fpa.ui.plash

import com.google.gson.JsonObject
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class SplashFragment : BaseFragment() {

    private val vm: SplashVM by lazy { activityVM(SplashVM::class) }

    private val v: SplashView by lazy { SplashView(this) }

    private val test: SplashTest by lazy { SplashTest(this, vm) }

    /**
     * [BaseFragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.splash
    }

    override fun onViewCreated() {
        v.onViewInit()
        test.onTestInit()
    }

    override fun onLiveDataObserve() {
        vm.paymentInfo.observe {
            onPaymentData(it)
        }
    }

    /**
     * [SplashFragment] properties
     */
    private fun onPaymentData(obj: JsonObject?) {
        when (obj) {
            null -> {
                v.stopPaymentRemaining()
                v.animateStopRemaining()
            }
            else -> {
                v.animateStartRemaining {
                    navigate(MainDirections.actionGlobalPaymentFragment())
                }
                v.startPaymentRemaining {
                    vm.paymentInfo.value = null
                }
            }
        }
    }

}