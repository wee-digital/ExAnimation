package wee.digital.fpa.ui.device

import android.view.View
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainDialog
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.base.viewModel
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.library.extension.*

class DeviceFragment : MainDialog() {

    private val deviceView by lazy { DeviceView(this) }

    private val deviceVM by lazy { viewModel(DeviceVM::class) }

    /**
     * [BaseDialog] override
     */
    override fun layoutResource(): Int {
        return R.layout.device
    }

    override fun onViewCreated() {
        deviceView.onViewInit()
        deviceEditTextName.addEditorActionListener(EditorInfo.IME_ACTION_DONE) {
            deviceEditTextName.clearFocus()
            deviceView.showProgress()
            onRegisterDevice()
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.qrCode.observe {
            it ?: return@observe
            deviceView.onBindStation(it)
        }
        deviceVM.nameErrorLiveData.observe {
            deviceView.onNameError(it)
        }
        deviceVM.successLiveData.observe {
            onRegisterSuccess()
        }
        deviceVM.failureLiveData.observe {
            onRegisterError()
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            deviceViewBack -> {
                dismiss()
                navigate(Main.qr)
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
        deviceView.onNameError(null)
        val s = deviceEditTextName.trimText
        deviceVM.registerDevice(s, sharedVM.qrCode.value)
    }

    private fun onRegisterSuccess() {
        deviceView.hideProgress()
        sharedVM.message.value = MessageArg(
                icon = R.mipmap.img_checked_flat,
                title = string(R.string.device_register_success),
                button = string(R.string.device_register_finish),
                message = string(R.string.register_success)
                        .format("pos.facepay.vn".bold().color("#378AE1")),
                onClose = {
                    sharedVM.syncDeviceInfo()
                }
        )
        dismiss()
        navigate(Main.message)
    }

    private fun onRegisterError() {
        deviceView.hideProgress()
        sharedVM.message.value = MessageArg(
                title = string(R.string.device_register_failed),
                button = string(R.string.device_register_fail),
                message = string(R.string.register_failed)
                        .format("Hotline: 1900 2323".bold().color("#212121"))
        )
        dismiss()
        navigate(Main.message)
    }

}