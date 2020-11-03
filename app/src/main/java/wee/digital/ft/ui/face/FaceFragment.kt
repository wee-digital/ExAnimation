package wee.digital.ft.ui.face

import wee.digital.ft.R
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainFragment
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.confirm.ConfirmArg
import wee.digital.ft.ui.onPaymentCancel
import wee.digital.ft.ui.onPaymentFailed

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