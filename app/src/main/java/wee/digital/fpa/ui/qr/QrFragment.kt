package wee.digital.fpa.ui.qr

import android.view.View
import kotlinx.android.synthetic.main.qr.*
import wee.digital.fpa.R
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainDialog
import wee.digital.fpa.ui.base.activityVM

class QrFragment : MainDialog(), ScanQRCode.QRCodeProcessingListener {

    private val qrVM by lazy { activityVM(QrVM::class) }

    private val qrView by lazy { QrView(this) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun onViewCreated() {
        qrView.onViewInit()
    }

    override fun onLiveDataObserve() {
        qrVM.messageLiveData.observe {
            qrView.onBindMessage(it)
        }
        qrVM.qrLiveData.observe {
            sharedVM.qrCode.value = it
            dismiss()
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