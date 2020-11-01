package wee.digital.fpa.ui.device

import android.view.View
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.connectVM
import wee.digital.fpa.ui.mainVM
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.messageVM
import wee.digital.library.extension.*
import kotlin.reflect.KClass

class DeviceFragment : Main.Dialog<DeviceVM>() {

    private val deviceView by lazy { DeviceView(this) }

    /**
     * [BaseDialog] override
     */
    override fun layoutResource(): Int {
        return R.layout.device
    }

    override fun localViewModel(): KClass<DeviceVM> {
        return DeviceVM::class
    }

    override fun onViewCreated() {
        deviceView.onViewInit()
        deviceEditTextName.addEditorActionListener(EditorInfo.IME_ACTION_DONE) {
            deviceEditTextName.clearFocus()
            deviceView.showProgress()
            onRegisterDevice()
        }
    }

    override fun onLiveEventChanged(event: Int) {
        when (event) {
            DeviceEvent.NAME_INVALID -> {
                deviceView.onNameError()
            }
            DeviceEvent.REGISTER_SUCCESS -> {
                onRegisterSuccess()
            }
            DeviceEvent.REGISTER_ERROR -> {
                onRegisterError()
            }
        }
    }

    override fun onLiveDataObserve() {
        connectVM.objQRCode.observe {
            deviceView.onBindStation(it)
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
        deviceTextViewError.text = null
        val s = deviceEditTextName.trimText
        localVM.registerDevice(s, connectVM.objQRCode.value)
    }

    private fun onRegisterSuccess() {
        deviceView.hideProgress()
        dismiss()
        messageVM.arg.value = MessageArg(
                icon = R.mipmap.img_checked_flat,
                title = string(R.string.device_register_success),
                button = string(R.string.device_register_finish),
                message = string(R.string.register_success)
                        .format("pos.facepay.vn".bold().color("#378AE1")),
                onClose = {
                    it.mainVM.syncDeviceInfo()
                }
        )
        navigate(Main.message)
    }

    private fun onRegisterError() {
        deviceView.hideProgress()
        dismiss()
        messageVM.arg.value = MessageArg(
                title = string(R.string.device_register_failed),
                button = string(R.string.device_register_fail),
                message = string(R.string.register_failed)
                        .format("Hotline: 1900 2323".bold().color("#212121"))
        )
        navigate(Main.message)
    }


}