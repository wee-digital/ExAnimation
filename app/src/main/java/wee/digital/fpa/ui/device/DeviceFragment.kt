package wee.digital.fpa.ui.device

import android.view.View
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.connect.ConnectVM
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.message.MessageVM
import wee.digital.library.extension.addEditorActionListener
import wee.digital.library.extension.hideKeyboard
import wee.digital.library.extension.trimText

class DeviceFragment : Main.Dialog() {

    private val connectVM by lazy { activityVM(ConnectVM::class) }

    private val deviceVM by lazy { viewModel(DeviceVM::class) }

    private val deviceView by lazy { DeviceView(this) }

    /**
     * [BaseDialog] override
     */
    override fun layoutResource(): Int {
        return R.layout.device
    }

    override fun onViewCreated() {
        deviceView.onViewInit()
        deviceEditTextName.addEditorActionListener(EditorInfo.IME_ACTION_DONE) {
            onRegisterDevice()
        }
    }

    override fun onLiveDataObserve() {
        connectVM.objQRCode.observe {
            deviceView.onBindStation(it)
        }
        deviceVM.nameError.observe {
            deviceView.onNameError(it)
        }
        deviceVM.registerError.observe {
            onRegisterError(it)
        }
        deviceVM.registerSuccess.observe {
            onRegisterSuccess(it)
        }
        deviceVM.progressVisible.observe {
            deviceView.onProgressChanged(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            deviceViewBack -> {
                dismiss()
                navigate(MainDirections.actionGlobalQrFragment())
            }
            deviceViewClose -> {
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
        hideKeyboard()
        deviceTextViewError.text = null
        val s = deviceEditTextName.trimText
        deviceVM.registerDevice(s, connectVM.objQRCode.value)
    }

    private fun onRegisterSuccess(arg: MessageArg) {
        mainVM.syncDeviceInfo()
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