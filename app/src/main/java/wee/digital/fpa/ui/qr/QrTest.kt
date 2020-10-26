package wee.digital.fpa.ui.qr

import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.qr.*
import wee.digital.library.extension.put
import kotlin.random.Random

class QrTest(private val v: QrFragment,
             private val vm: QrVM) {

    private val tests = listOf(
            {
                vm.qrCode.value = JsonObject()
            },
            {
                vm.qrCode.value = JsonObject().also {
                    it.put("FullName", "FamilyMart")
                }
            }
    )

    fun onTestInit() {
        v.viewTest.setOnClickListener {
            tests[Random.nextInt(0, tests.lastIndex)]()
        }
    }


}