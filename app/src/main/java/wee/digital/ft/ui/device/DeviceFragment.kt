package wee.digital.ft.ui.device

import android.view.View
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.device.*
import wee.digital.ft.R
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.base.BaseDialog
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.message.MessageArg
import wee.digital.library.extension.*

class DeviceFragment : MainDialog() {

    private val deviceVM by lazy { viewModel(DeviceVM::class) }

    /**
     * [BaseDialog] override
     */
    override fun layoutResource(): Int {
        return R.layout.device
    }

    override fun onViewCreated() {
        addClickListener(deviceViewBack, deviceViewClose, deviceViewRegister)
        deviceView.onViewInit()
        deviceEditTextName.addEditorActionListener(EditorInfo.IME_ACTION_DONE) {
            deviceEditTextName.clearFocus()
            hideKeyboard()
            onRegisterDevice()
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.qrCode.observe {
            it ?: return@observe
            deviceView.onBindStation(it)
        }
        deviceVM.nameErrorLiveData.observe {
            deviceView.hideProgress()
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
        deviceView.showProgress()
        deviceView.onNameError(null)
        deviceVM.registerDevice(deviceEditTextName.trimText, sharedVM.qrCode.value)
    }

    private fun onRegisterSuccess() {
        deviceView.hideProgress()
        dismissAllowingStateLoss()
        sharedVM.message.value = MessageArg().apply {
            icon = R.mipmap.img_checked_flat
            title = "Đăng ký thiết bị thành công"
            message = "Xem thông tin các thiết bị đã liên kết trong phần\nquản lý thiết bị tại %s"
                    .format("pos.facepay.vn".bold().color("#378AE1"))
            buttonClose = "Hoàn tất"
            onClose = {
                sharedVM.syncDeviceInfo()
            }
        }
    }

    private fun onRegisterError() {
        deviceView.hideProgress()
        dismissAllowingStateLoss()
        sharedVM.message.value = MessageArg().apply {
            title = "Đăng ký thiết bị không\nthành công"
            message = "Có lỗi phát sinh, bạn vui lòng thử lại lần nữa"
            buttonClose = "Quét lại mã QR"
            onClose = {
                it.navigate(Main.qr)
            }
        }
    }

}