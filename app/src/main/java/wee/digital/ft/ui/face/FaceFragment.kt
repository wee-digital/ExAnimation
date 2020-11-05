package wee.digital.ft.ui.face

import kotlinx.android.synthetic.main.face.*
import wee.digital.ft.R
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainFragment
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.confirm.ConfirmArg
import wee.digital.ft.ui.message.MessageArg
import wee.digital.library.extension.bold
import wee.digital.library.extension.color

class FaceFragment : MainFragment() {

    private val faceVM by lazy { viewModel(FaceVM::class) }

    /**
     * [MainFragment] override
     */
    override fun layoutResource(): Int {
        return R.layout.face
    }

    override fun onViewCreated() {
        faceView.onViewInit(this)
        faceView.onFaceEligible = { bitmap, pointData, dataCollect ->
            sharedVM.stopTimeout()
            faceVM.verifyFace(bitmap, pointData, dataCollect, sharedVM.payment.value)
        }
    }

    override fun onLiveDataObserve() {
        sharedVM.timeoutColor.value = R.color.colorTimeoutFace
        sharedVM.startTimeout(Timeout.FACE_VERIFY, MessageArg.timedOutError)
        faceVM.successLiveData.observe {
            onFaceVerifySuccess(it)
        }
        faceVM.failureLiveData.observe {
            sharedVM.startTimeout(it)
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
        val confirmArg = ConfirmArg(
                headerGuideline = R.id.guidelineFace,
                title = "Bạn chưa đăng ký tài khoản Facepay",
                message = "Bạn vui lòng thử lại hoặc tải ứng dụng ${("Facepay").bold().color("#3082D8")}<br/>để đăng ký tài khoản",
                buttonAccept = "Thử lại",
                onAccept = {
                    faceView.animateOnStartFaceReg()
                    it.sharedVM.startTimeout(Timeout.FACE_VERIFY, MessageArg.timedOutError)
                },
                buttonDeny = "Hủy bỏ giao dịch",
                onDeny = {
                    it.sharedVM.onPaymentCancel()
                }
        )
        sharedVM.startTimeout(Timeout.FACE_VERIFY, confirmArg)
    }


}