package wee.digital.ft.ui.qr

import android.view.View
import kotlinx.android.synthetic.main.qr.*
import wee.digital.ft.R
import wee.digital.ft.camera.ScanQRCode
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.base.viewModel

class QrFragment : MainDialog(), ScanQRCode.QRCodeProcessingListener {

    private val qrVM by lazy { viewModel(QrVM::class) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun onViewCreated() {
        addClickListener(qrViewClose)
        qrView.onViewInit(this, this)
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
        qrVM.progressLiveData.observe {
            qrView.onBindProgress(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            qrViewClose -> {
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