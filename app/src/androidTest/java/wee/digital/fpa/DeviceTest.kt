package wee.digital.fpa

import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.ui.device.DeviceFragment
import wee.digital.fpa.ui.device.DeviceVM

class DeviceTest(private val v: DeviceFragment,
                 private val vm: DeviceVM) {


    fun onTestInit() {
        v.viewTest.setOnClickListener {
            vm.registerError.value = null//"test error"
        }
    }


}