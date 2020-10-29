package wee.digital.fpa.ui.face

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.arg.ConfirmArg
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.confirm.ConfirmVM
import wee.digital.fpa.ui.payment.PaymentVM

class FaceFragment : Main.Fragment() {

    private val paymentVM: PaymentVM by lazy { activityVM(PaymentVM::class) }

    private val faceVM: FaceVM by lazy { activityVM(FaceVM::class) }

    private val faceView: FaceView by lazy { FaceView(this) }

    override fun layoutResource(): Int = R.layout.face

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

    private fun onFaceVerifySuccess() {
        timeoutVM.stopTimeout()
        faceView.animateOnFaceCaptured()
        navigate(MainDirections.actionGlobalPinFragment())
    }

    private fun onFaceVerifyError(it: ConfirmArg) {


    }

    private fun onPaymentDeny() {
        paymentVM.paymentArg.value = null
        timeoutVM.stopTimeout()
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