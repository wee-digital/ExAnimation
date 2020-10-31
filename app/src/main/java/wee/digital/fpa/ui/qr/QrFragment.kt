package wee.digital.fpa.ui.qr

import android.view.View
import kotlinx.android.synthetic.main.qr.*
import wee.digital.fpa.R
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.ui.Main

class QrFragment : Main.Dialog(), ScanQRCode.QRCodeProcessingListener {

    private val qrVM by lazy { viewModel(QrVM::class) }

    private val qrView by lazy { QrView(this) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun onViewCreated() {
        qrView.onViewInit()
    }

    override fun onLiveDataObserve() {
        qrVM.message.observe {
            qrView.onBindMessage(it)
        }
        qrVM.qrCode.observe {
            dismiss()
            connectVM.objQRCode.value = it
            navigate(Main.device)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            dialogViewClose -> {
                navigateUp()
            }
        }
    }

    /**
     * [ScanQRCode.QRCodeProcessingListener] implement
     */
    override fun onResult(result: String) {
        qrVM.checkQRCode(result)
    }

}