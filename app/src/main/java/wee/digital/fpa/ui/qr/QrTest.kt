package wee.digital.fpa.ui.qr

import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.qr.*
import wee.digital.library.extension.put

class QrTest(private val v: QrFragment,
             private val vm: QrVM) {

    fun onTestInit() {
        v.viewTest.setOnClickListener {
            vm.qrCode.value = JsonObject().also {
                it.put("FullName", "FamilyMart")
            }
        }
    }


}