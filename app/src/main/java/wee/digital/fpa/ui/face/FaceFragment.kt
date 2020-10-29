package wee.digital.fpa.ui.face

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.arg.ConfirmArg
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.confirm.ConfirmVM

class FaceFragment : Main.Fragment() {


    private val faceVM: FaceVM by lazy { activityVM(FaceVM::class) }

    private val faceView: FaceView by lazy { FaceView(this) }

    override fun layoutResource(): Int = R.layout.face

    override fun onViewCreated() {
        faceView.onViewInit()
        faceView.onFaceEligible = { bitmap, pointData, dataCollect ->
            timeoutVM.stopTimeout()
            faceVM.verifyFace(bitmap, pointData, dataCollect)
        }
    }

    override fun onLiveDataObserve() {
        timeoutVM.startTimeout(Timeout.FACE_TIMEOUT)
        timeoutVM.second.observe {
            faceView.onBindRemainingText(it)
        }
        timeoutVM.inTheEnd.observe {
            if (it) onPaymentDeny()
        }
        faceVM.verifyError.observe {
            onFaceVerifyError(it)
        }
        faceVM.verifySuccess.observe {
            onFaceVerifySuccess()
        }

    }

    private fun onFaceVerifySuccess() {
        timeoutVM.stopTimeout()
        faceView.animateOnFaceCaptured()
        navigate(MainDirections.actionGlobalPinFragment())
    }

    private fun onFaceVerifyError(it: ConfirmArg) {
        onFaceVerifySuccess()
        return
        it.onAccept = {
            timeoutVM.startTimeout(Timeout.FACE_TIMEOUT)
            faceView.animateOnStartFaceReg()
        }
        it.onDeny = {
            navigate(MainDirections.actionGlobalSplashFragment()) {
                setLaunchSingleTop()
            }
        }
        faceView.animateOnFaceCaptured()
        timeoutVM.startTimeout(Timeout.FACE_TIMEOUT)
        activityVM(ConfirmVM::class).arg.value = it
        navigate(MainDirections.actionGlobalConfirmFragment())

    }

    private fun onPaymentDeny() {
        mainVM.paymentArg.value = null
        timeoutVM.stopTimeout()
        navigate(MainDirections.actionGlobalSplashFragment()) {
            setLaunchSingleTop()
        }
    }


}