package wee.digital.fpa.ui.qr

import android.view.View
import kotlinx.android.synthetic.main.qr.*
import wee.digital.fpa.R
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.connect.ConnectArg
import wee.digital.fpa.ui.connect.ConnectVM

class QrFragment : BaseDialog(), ScanQRCode.QRCodeProcessingListener {

    private val connectVM by lazy { activityVM(ConnectVM::class) }

    private val vm by lazy { viewModel(QrVM::class) }

    private val v by lazy { QrView(this) }

    private val test by lazy { QrTest(this, vm) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun onViewCreated() {
        test.onTestInit()
        v.onViewInit()
    }

    override fun onLiveDataObserve() {
        vm.message.observe {
            v.onBindMessage(it)
        }
        vm.qrCode.observe {
            dismiss()
            connectVM.arg.value = ConnectArg(qr = it)
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
        vm.checkQRCode(result)
    }

}