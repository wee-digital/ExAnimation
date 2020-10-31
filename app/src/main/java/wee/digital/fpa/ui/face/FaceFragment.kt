package wee.digital.fpa.ui.face

import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.*
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.message.MessageArg
import kotlin.reflect.KClass

class FaceFragment : Main.Fragment<FaceVM>() {

    private val faceView by lazy { FaceView(this) }

    /**
     * [Main.Fragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.face
    }

    override fun localViewModel(): KClass<FaceVM> {
        return FaceVM::class
    }

    override fun onViewCreated() {
        faceView.onViewInit()
        faceView.onFaceEligible = { bitmap, pointData, dataCollect ->
            timeoutVM.stopTimeout()
            localVM.verifyFace(bitmap, pointData, dataCollect, paymentVM.arg.value)
        }
    }

    override fun onLiveEventChanged(event: Int) {
        when (event) {
            FaceEvent.VERIFY_SUCCESS -> {
                onFaceVerifySuccess()
            }
            FaceEvent.VERIFY_RETRY -> {
                onRetryVerify()
            }
            FaceEvent.VERIFY_FAILED -> {
                onPaymentFailed(MessageArg.paymentCancel)

            }
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
    }

    /**
     * [FaceFragment] properties
     */
    private fun onFaceVerifySuccess() {
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