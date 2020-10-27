package wee.digital.fpa.ui.device

import android.view.View
import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.message.MessageVM
import wee.digital.library.extension.trimText

class DeviceFragment : BaseDialog() {

    private val vm by lazy { viewModel(DeviceVM::class) }

    private val v by lazy { DeviceView(this) }

    /**
     * [BaseDialog] override
     */
    override fun layoutResource(): Int {
        return R.layout.device
    }

    override fun onViewCreated() {
        v.onViewInit()
    }

    override fun onLiveDataObserve() {
        vm.objQRCode.observe {
            v.onBindStation(it)
        }
        vm.nameError.observe {
            v.onNameError(it)
        }
        vm.registerError.observe {
            onRegisterError(it)
        }
        vm.registerSuccess.observe {
            onRegisterSuccess(it)
        }
        vm.progressVisible.observe {
            v.onProgressChanged(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            deviceViewBack, deviceViewClose -> {
                dismiss()
            }
            deviceViewRegister -> {
                onRegisterDevice()
            }
        }
    }

    /**
     * [DeviceFragment] properties
     */
    private fun onRegisterDevice() {
        deviceTextViewError.text = null
        val s = deviceEditTextName.trimText
        vm.registerDevice(s)
    }

    private fun onRegisterSuccess(arg: MessageArg) {
        dismiss()
        activityVM(MessageVM::class).arg.value = arg
        navigate(MainDirections.actionGlobalMessageFragment())
    }

    private fun onRegisterError(arg: MessageArg) {
        dismiss()
        activityVM(MessageVM::class).arg.value = arg
        navigate(MainDirections.actionGlobalMessageFragment())
    }

}