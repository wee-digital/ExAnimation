package wee.digital.fpa.ui.face

import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.repository.dto.FaceArg
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.message.MessageArg

class FaceFragment : Main.Fragment() {

    /**
     * [Main.Fragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.face
    }

    override fun onViewCreated() {
        faceView.onViewInit()
        faceView.onFaceEligible = { bitmap, pointData, dataCollect ->
            timeoutVM.stopTimeout()
            faceVM.verifyFace(bitmap, pointData, dataCollect, paymentVM.arg.value)
        }
    }

    override fun onLiveDataObserve() {
        timeoutVM.startTimeout(Timeout.FACE_VERIFY)
        timeoutVM.second.observe {
            faceView.onBindRemainingText(it)
        }
        timeoutVM.inTheEnd.observe {
            it ?: return@observe
            onPaymentCancel()
        }
        faceVM.faceArg.observe {
            onFaceVerifySuccess(it)
        }
        faceVM.verifyError.observe {
            if (it) onPaymentError(MessageArg.paymentCancel)
        }
        faceVM.verifyRetry.observe {
            if (it) onRetryVerify()
        }
    }


    /**
     * [FaceFragment] properties
     */
    private val faceView by lazy { FaceView(this) }

    private fun onFaceVerifySuccess(it: FaceArg?) {
        it ?: return
        timeoutVM.stopTimeout()
        faceView.animateOnFaceCaptured()
        navigate(Main.pin)
    }

    private fun onRetryVerify() {
        val arg = ConfirmArg(
                headerGuideline = R.id.guidelineFace,
                title = "Tài khoản không tồn tại",
                message = "Bạn vui lòng đăng ký tài khoản Facepay trước khi thực hiện thanh toán",
                buttonAccept = "Thử lại",
                onAccept = {
                    faceView.animateOnStartFaceReg()
                    timeoutVM.startTimeout(Timeout.FACE_VERIFY)
                },
                buttonDeny = "Hủy bỏ giao dịch",
                onDeny = {
                    onPaymentCancel()
                }
        )
        faceView.animateOnFaceCaptured()
        timeoutVM.startTimeout(Timeout.FACE_VERIFY)
        confirmVM.arg.value = arg
        navigate(Main.confirm)
    }


}