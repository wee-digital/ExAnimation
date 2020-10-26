package wee.digital.fpa.ui.plash

import com.google.gson.JsonObject
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class SplashFragment : BaseFragment() {

    private val vm by lazy { activityVM(SplashVM::class) }

    private val v by lazy { SplashView(this) }

    private val test by lazy { SplashTest(this, vm) }

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
        vm.checkLocalDeviceInfo()
        vm.paymentInfo.observe {
            onPaymentData(it)
        }
        vm.hasDeviceInfo.observe {
            onHasDevice(it)
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
                navigate(MainDirections.actionGlobalPaymentFragment())
                v.animateStartRemaining()
                v.startPaymentRemaining {
                    vm.paymentInfo.value = null
                }
            }
        }
    }

    private fun onHasDevice(hasDevice: Boolean) {
        if (!hasDevice) {
            navigate(MainDirections.actionGlobalConnectFragment())
        }
    }

}