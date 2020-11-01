package wee.digital.fpa.ui.qr

import android.view.View
import kotlinx.android.synthetic.main.qr.*
import wee.digital.fpa.R
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.connectVM
import kotlin.reflect.KClass

class QrFragment : Main.Dialog<QrVM>(), ScanQRCode.QRCodeProcessingListener {

    private val qrView by lazy { QrView(this) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun localViewModel(): KClass<QrVM> {
        return QrVM::class
    }

    override fun onViewCreated() {
        qrView.onViewInit()
    }

    override fun onLiveDataObserve() {
        localVM.message.observe {
            qrView.onBindMessage(it)
        }
        localVM.qrCode.observe {
            dismiss()
            connectVM.objQRCode.value = it
            navigate(Main.device)
        }
    }

    override fun onLiveEventChanged(event: Int) {
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
        localVM.checkQRCode(result)
    }


}