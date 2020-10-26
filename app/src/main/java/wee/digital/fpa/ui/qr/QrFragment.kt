package wee.digital.fpa.ui.qr

import android.view.View
import kotlinx.android.synthetic.main.qr.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.device.DeviceArg
import wee.digital.fpa.ui.device.DeviceVM

class QrFragment : BaseDialog(), ScanQRCode.QRCodeProcessingListener {

    private val vm by lazy { viewModel(QrVM::class) }

    private val v by lazy { QrView(this) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun onViewCreated() {
        v.onViewInit()
    }

    override fun onLiveDataObserve() {
        vm.message.observe {
            v.onBindMessage(it)
        }
        vm.progress.observe {
            v.onBindProgress(it)
        }
        vm.qrCode.observe {
            dismiss()
            activityVM(DeviceVM::class).arg.value = DeviceArg(it)
            navigate(MainDirections.actionGlobalDeviceFragment())
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            dialogViewClose -> {
                dismiss()
            }
        }
    }

    /**
     * [ScanQRCode.QRCodeProcessingListener] implement
     */
    override fun onResult(result: String) {
        vm.checkQRCode(result)
    }

}