package wee.digital.fpa.ui.plash

import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.splash.*
import wee.digital.library.extension.put

class SplashTest(private val testCtx: SplashFragment,
                 private val v: SplashView,
                 private val vm: SplashVM) {

    private var isPaymentRequested: Boolean = false

    fun onTestInit() {
        testCtx.splashImageViewLogo.setOnClickListener {
            if (isPaymentRequested) {
                onPaymentDismiss()
            } else {
                onPaymentRequest()
            }
            isPaymentRequested = !isPaymentRequested
        }
    }

    private fun onPaymentRequest() {
        vm.paymentInfo.postValue(JsonObject().apply {
            put("name", "GS25")
            put("amount", "365000")
        })
    }

    private fun onPaymentDismiss() {
        vm.paymentInfo.postValue(null)
    }

}