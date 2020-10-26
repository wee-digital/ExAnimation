package wee.digital.fpa.ui.device

import kotlinx.android.synthetic.main.device.*
import kotlin.random.Random

class DeviceTest(private val v: DeviceFragment,
                 private val vm: DeviceVM) {

    private val tests = listOf(
            {
                vm.registerError.value = null
            },
            {
                vm.registerError.value = "test error"
            }
    )

    fun onTestInit() {
        v.viewTest.setOnClickListener {
            tests[Random.nextInt(0, tests.lastIndex)]()
        }
    }


}