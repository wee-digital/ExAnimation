package wee.digital.fpa.ui.face

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.arg.ConfirmArg
import wee.digital.fpa.ui.confirm.ConfirmVM

class FaceFragment : Main.Fragment() {


    private val faceVM: FaceVM by lazy { activityVM(FaceVM::class) }

    private val faceView: FaceView by lazy { FaceView(this) }

    override fun layoutResource(): Int = R.layout.face

    override fun onViewCreated() {
        faceView.onViewInit()

        faceView.onFaceEligible = { bitmap, pointData, dataCollect ->
            remainingVM.stopTimeout()
            faceVM.verifyFace(bitmap, pointData, dataCollect)
        }
    }

    override fun onLiveDataObserve() {
        remainingVM.startTimeout(Timeout.FACE_TIMEOUT)
        remainingVM.interval.observe {
            faceView.onBindRemainingText(it)
            if (it == 0) navigate(MainDirections.actionGlobalSplashFragment()) {
                setLaunchSingleTop()
            }
        }
        faceVM.verifyError.observe {
            onFaceVerifyError(it)
        }
        faceVM.verifySuccess.observe {
            onFaceVerifySuccess()
        }

    }

    private fun onFaceVerifySuccess() {
        faceView.animateOnFaceCaptured()
        navigate(MainDirections.actionGlobalPinFragment())
    }

    private fun onFaceVerifyError(it: ConfirmArg) {
        onFaceVerifySuccess()
        return
        it.onAccept = {
            remainingVM.startTimeout(Timeout.FACE_TIMEOUT)
            faceView.animateOnStartFaceReg()
        }
        it.onDeny = {
            navigate(MainDirections.actionGlobalSplashFragment()) {
                setLaunchSingleTop()
            }
        }
        faceView.animateOnFaceCaptured()
        remainingVM.startTimeout(Timeout.FACE_TIMEOUT)
        activityVM(ConfirmVM::class).arg.value = it
        navigate(MainDirections.actionGlobalConfirmFragment())

    }


}