package wee.digital.fpa.ui.device

import android.view.View
import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM

class DeviceFragment : BaseDialog() {

    private val vm by lazy { activityVM(DeviceVM::class) }

    private val v by lazy { DeviceView(this) }

    private val test by lazy { DeviceTest(this, vm) }

    override fun layoutResource(): Int = R.layout.device

    override fun onViewCreated() {
        v.onViewInit()
        test.onTestInit()
    }

    override fun onLiveDataObserve() {
        vm.arg.observe {
            v.onBindStation(it)
        }
        vm.nameError.observe {
            v.onNameError(it)
        }
        vm.registerError.observe {
            v.onRegisterError(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            deviceViewBack -> navigateUp()

            deviceViewClose -> navigate(MainDirections.actionGlobalSplashFragment())

            deviceViewRegister -> onRegisterDevice()
        }
    }

    private fun onRegisterDevice() {
        deviceTextViewError.text = null
        val s = deviceEditTextName.text.toString().trimEnd()
        deviceEditTextName?.setText(s)
        deviceEditTextName?.setSelection(s.length)
        vm.validateOnRegisterDevice(deviceEditTextName.text?.toString())
    }

}