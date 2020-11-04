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
        val animation = ObjectAnimator.ofFloat(frgQrImgAnimBot, "translationY", 0f, 500f)
        animation.duration = 1000
        animation.start()
        animation.addListener(object : Animator.AnimatorListener {

            override fun onAnimationEnd(animation: Animator?) {
                frgQrImgAnimBot?.post { frgQrImgAnimBot.rotationX = 180f }
                animationTop()
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}

        })
    }

    private fun animationTop() {
        val animation = ObjectAnimator.ofFloat(frgQrImgAnimBot, "translationY", 500f, 0f)
        animation.duration = 1000
        animation.start()
        animation.addListener(object : Animator.AnimatorListener {

            override fun onAnimationEnd(animation: Animator?) {
                frgQrImgAnimBot?.post { frgQrImgAnimBot.rotationX = 0f }
                animationBot()
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}

        })
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