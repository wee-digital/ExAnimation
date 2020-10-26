package wee.digital.fpa.ui.device

import android.view.View
import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.ui.base.BaseFragment

class DeviceFragment : BaseFragment() {

    private val vm by lazy { viewModel(DeviceVM::class) }

    private val v by lazy { DeviceView(this) }

    override fun layoutResource(): Int = R.layout.device

    override fun onViewCreated() {
        v.onViewInit()
    }

    override fun onLiveDataObserve() {
        vm.syncStationName()
        vm.stationName.observe{
            v.onBindStation(it)
        }
        vm.errorText.observe {
            v.onBindError(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            deviceViewBack -> nav.popBackStack()

            deviceViewClose -> nav.navigate(MainDirections.actionGlobalSplashFragment())

            deviceViewRegister -> onRegisterDevice()
        }
    }

    private fun onRegisterDevice() {
        deviceTextViewError.text = null
        val s = deviceEditTextName.text.toString().trimEnd()
        deviceEditTextName?.setText(s)
        deviceEditTextName?.setSelection(s.length)
        Shared.deviceInfo.value?.name = deviceEditTextName.text.toString()
        vm.validateOnRegisterDevice(s)
    }

}