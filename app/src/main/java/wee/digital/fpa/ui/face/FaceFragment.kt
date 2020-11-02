package wee.digital.fpa.ui.face

import wee.digital.fpa.R
import wee.digital.fpa.shared.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainFragment
import wee.digital.fpa.ui.base.viewModel
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.onPaymentCancel
import wee.digital.fpa.ui.onPaymentFailed

class FaceFragment : MainFragment() {

    private val faceVM by lazy { viewModel(FaceVM::class) }

    private val faceView by lazy { FaceView(this) }

    /**
     * [MainFragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.face
    }

    override fun onViewCreated() {
        faceView.onViewInit()
        faceView.onFaceEligible = { bitmap, pointData, dataCollect ->
            sharedVM.stopTimeout()
            faceVM.verifyFace(bitmap, pointData, dataCollect, sharedVM.payment.value)
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.timeoutColor.value = R.color.colorTimeoutFace
        sharedVM.startTimeout(Timeout.FACE_VERIFY)

        faceVM.successLiveData.observe {
            onFaceVerifySuccess(it)
        }
        faceVM.failureLiveData.observe {
            onPaymentFailed(it)
        }
        faceVM.retriesLiveData.observe {
            onRetryVerify()
        }
    }

    /**
     * [FaceFragment] properties
     */
    private fun onFaceVerifySuccess(it: FaceArg) {
        sharedVM.stopTimeout()
        sharedVM.face.value = it
        faceView.animateOnFaceCaptured()
        navigate(Main.pin)
    }

    private fun onRetryVerify() {
        faceView.animateOnFaceCaptured()
        sharedVM.startTimeout(Timeout.FACE_VERIFY)
        sharedVM.confirm.value = ConfirmArg(
                headerGuideline = R.id.guidelineFace,
                title = "Tài khoản không tồn tại",
                message = "Bạn vui lòng đăng ký tài khoản Facepay trước khi thực hiện thanh toán",
                buttonAccept = "Thử lại",
                onAccept = {
                    faceView.animateOnStartFaceReg()
                    sharedVM.startTimeout(Timeout.FACE_VERIFY)
                },
                buttonDeny = "Hủy bỏ giao dịch",
                onDeny = {
                    onPaymentCancel()
                }
        )
        navigate(Main.confirm)
    }


}