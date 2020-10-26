package wee.digital.fpa.ui.device

import kotlinx.android.synthetic.main.device.*

class DeviceTest(private val v: DeviceFragment,
                 private val vm: DeviceVM) {


    fun onTestInit() {
        v.viewTest.setOnClickListener {
            vm.registerError.value = "test error"
        }
    }


}