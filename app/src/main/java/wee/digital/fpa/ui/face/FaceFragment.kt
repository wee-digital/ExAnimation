package wee.digital.fpa.ui.face

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.confirm.ConfirmVM
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.message.MessageVM
import wee.digital.fpa.ui.payment.PaymentVM

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
            faceVM.verifyFace(bitmap, pointData, dataCollect, paymentVM.paymentArg.value)
        }
    }

    override fun onLiveDataObserve() {
        timeoutVM.startTimeout(Timeout.FACE_VERIFY)
        timeoutVM.second.observe {
            faceView.onBindRemainingText(it)
        }
        timeoutVM.inTheEnd.observe {
            if (it) onPaymentDeny()
        }
        faceVM.faceArg.observe {
            onFaceVerifySuccess()
        }
        faceVM.verifyError.observe {
            onFaceVerifyError(it)
        }
        faceVM.verifyRetry.observe {
            onFaceVerifyRetry(it)
        }
    }

    /**
     * [FaceFragment] properties
     */
    private val paymentVM: PaymentVM by lazy { activityVM(PaymentVM::class) }

    private val faceVM: FaceVM by lazy { activityVM(FaceVM::class) }

    private val faceView: FaceView by lazy { FaceView(this) }

    private fun onFaceVerifySuccess() {
        timeoutVM.stopTimeout()
        faceView.animateOnFaceCaptured()
        navigate(MainDirections.actionGlobalPinFragment())
    }

    private fun onFaceVerifyError(it: MessageArg) {
        paymentVM.paymentArg.postValue(null)
        timeoutVM.startTimeout(Timeout.PAYMENT_DENIED)
        activityVM(MessageVM::class).arg.value = it
        navigate(MainDirections.actionGlobalMessageFragment())
    }

    private fun onPaymentDeny() {
        paymentVM.paymentArg.postValue(null)
        navigate(MainDirections.actionGlobalSplashFragment()) {
            setNoneAnim()
            setLaunchSingleTop()
        }
    }

    private fun onFaceVerifyRetry(it: ConfirmArg) {
        it.onAccept = {
            faceView.animateOnStartFaceReg()
            timeoutVM.startTimeout(Timeout.FACE_VERIFY)
        }
        it.onDeny = {
            navigate(MainDirections.actionGlobalSplashFragment()) {
                setLaunchSingleTop()
            }
        }
        faceView.animateOnFaceCaptured()
        timeoutVM.startTimeout(Timeout.FACE_RETRY)
        activityVM(ConfirmVM::class).arg.value = it
        navigate(MainDirections.actionGlobalConfirmFragment())
    }


}