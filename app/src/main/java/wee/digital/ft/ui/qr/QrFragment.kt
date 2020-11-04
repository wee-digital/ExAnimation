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

    private val qrView by lazy { QrView(this) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun onViewCreated() {
        qrView.onViewInit()
    }

    override fun onLiveDataObserve() {
        qrVM.messageLiveData.observe {
            qrView.hideProgress()
            qrView.onBindMessage(it)
        }
        qrVM.qrLiveData.observe {
            qrView.hideProgress()
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
        qrView.showProgress()
        qrVM.checkQRCode(result)
    }


}