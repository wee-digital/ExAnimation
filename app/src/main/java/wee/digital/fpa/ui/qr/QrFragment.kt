package wee.digital.fpa.ui.qr

import wee.digital.fpa.R
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.ui.base.BaseDialog

class QrFragment : BaseDialog(), ScanQRCode.QRCodeProcessingListener {

    private val vm by lazy { viewModel(QrVM::class) }

    private val v by lazy { QrView(this) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun onViewCreated() {
        v.onViewInit()
        Shared.deviceInfo.postValue(DeviceInfoStore())
    }

    override fun onLiveDataObserve() {
        vm.message.observe {
            v.onBindMessage(it)
        }
    }

    /**
     * [ScanQRCode.QRCodeProcessingListener] implement
     */
    override fun onResult(result: String) {
        vm.checkQRCode(result)
    }

}