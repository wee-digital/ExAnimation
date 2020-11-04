package wee.digital.ft.ui.qr

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import kotlinx.android.synthetic.main.qr.*
import wee.digital.ft.R
import wee.digital.ft.camera.ScanQRCode
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.base.viewModel
import wee.digital.library.extension.SimpleAnimatorListener

class QrFragment : MainDialog(), ScanQRCode.QRCodeProcessingListener {

    private val qrVM by lazy { viewModel(QrVM::class) }

    private val qrView by lazy { QrView(this) }

    override fun layoutResource(): Int {
        return R.layout.qr
    }

    override fun onViewCreated() {
        qrView.onViewInit()
        animationBot()
    }

    private fun animationBot() {
        val animation = ObjectAnimator.ofFloat(qrViewScanAnim, "translationY", 0f, 500f)
        animation.duration = 1200
        animation.start()
        animation.addListener(object : SimpleAnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                qrViewScanAnim?.post { qrViewScanAnim.rotationX = 0f }
                animationTop()
            }
        })
    }

    private fun animationTop() {
        val animation = ObjectAnimator.ofFloat(qrViewScanAnim, "translationY", 500f, 0f)
        animation.duration = 1200
        animation.start()
        animation.addListener(object : SimpleAnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                qrViewScanAnim?.post { qrViewScanAnim.rotationX = 180f }
                animationBot()
            }
        })
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