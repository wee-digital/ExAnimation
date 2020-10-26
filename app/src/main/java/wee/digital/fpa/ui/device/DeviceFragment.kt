package wee.digital.fpa.ui.device

import android.view.View
import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.connect.ConnectArg
import wee.digital.fpa.ui.connect.ConnectVM
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.library.extension.bold
import wee.digital.library.extension.color
import wee.digital.library.extension.string

class DeviceFragment : BaseDialog() {

    private val connectVM by lazy { activityVM(ConnectVM::class) }

    private val vm by lazy { viewModel(DeviceVM::class) }

    private val v by lazy { DeviceView(this) }

    private val test by lazy { DeviceTest(this, vm) }

    override fun layoutResource(): Int = R.layout.device

    override fun onViewCreated() {
        v.onViewInit()
        test.onTestInit()
    }

    override fun onLiveDataObserve() {
        connectVM.arg.observe {
            v.onBindStation(it)
        }
        vm.nameError.observe {
            v.onNameError(it)
        }
        vm.registerError.observe {
            if (it.isNullOrEmpty()) {
                onRegisterSuccess()
            } else {
                onRegisterError(it)
            }
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

    private fun onRegisterDevice() {
        deviceTextViewError.text = null
        val s = deviceEditTextName.text.toString().trimEnd()
        deviceEditTextName?.setText(s)
        deviceEditTextName?.setSelection(s.length)
        val qr = connectVM.arg.value?.qr?.toString() ?: return
        vm.validateOnRegisterDevice(qr, deviceEditTextName.text?.toString())
    }

    private fun onRegisterSuccess() {
        dismiss()
        connectVM.arg.value = ConnectArg(message = MessageArg(
                icon = R.mipmap.img_checked_flat,
                title = "Đăng ký thiết bị thành công",
                button = "Hoàn tất",
                message = string(R.string.register_success).format("pos.facepay.vn".bold().color("#378AE1")),
                onClose = {

                }
        ))
    }

    private fun onRegisterError(s: String?) {
        dismiss()
        connectVM.arg.value = ConnectArg(message = MessageArg(
                icon = R.mipmap.img_x_mark_flat,
                title = "Đăng ký thiết bị không thành công",
                button = "Hoàn tất",
                message = s
        ))
    }

}