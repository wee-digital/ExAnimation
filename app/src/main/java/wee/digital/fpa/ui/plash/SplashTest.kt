package wee.digital.fpa.ui.plash

import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.splash.*
import wee.digital.library.extension.put

class SplashTest(private val v: SplashFragment,
                 private val vm: SplashVM) {


    fun onTestInit() {
        v.splashImageViewLogo.setOnClickListener {
            if (vm.paymentInfo.value == null) {
                vm.paymentInfo.value = JsonObject().apply {
                    put("name", "GS25")
                    put("amount", "365000")
                }
            } else {
                vm.paymentInfo.value = null
            }

        }
    }


}