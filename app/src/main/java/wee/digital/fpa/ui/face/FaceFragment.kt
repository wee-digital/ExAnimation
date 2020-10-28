package wee.digital.fpa.ui.face

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.confirm.ConfirmVM
import wee.digital.fpa.ui.vm.RemainingVM

class FaceFragment : Main.Fragment() {

    private val remainingVM by lazy { activityVM(RemainingVM::class) }

    private val faceVM: FaceVM by lazy { activityVM(FaceVM::class) }

    private val faceView: FaceView by lazy { FaceView(this) }

    override fun layoutResource(): Int = R.layout.face

    override fun onViewCreated() {
        faceView.onViewInit()
        faceView.startRemaining {
            navigate(MainDirections.actionGlobalSplashFragment()) {
                setLaunchSingleTop()
            }
        }
        faceView.onFaceEligible = { bitmap, pointData, dataCollect ->
            remainingVM.stopRemaining()
            faceVM.verifyFace(bitmap, pointData, dataCollect)
        }
    }

    override fun onLiveDataObserve() {
        faceVM.verifyError.observe {
            onFaceVerifyError(it)
        }
        faceVM.verifySuccess.observe {
            onFaceVerifySuccess()
        }
        remainingVM.interval.observe {
            faceView.onBindRemainingText(it)
        }
    }

    private fun onFaceVerifySuccess() {
        faceView.animateOnFaceCaptured()
        navigate(MainDirections.actionGlobalPinFragment())
    }

    private fun onFaceVerifyError(it: ConfirmArg) {
        faceView.animateOnFaceCaptured()
        it.onAccept = {
            remainingVM.startRemaining(10)
            faceView.animateOnStartFaceReg {
                faceView.hasFaceDetect = true
            }
        }
        it.onDeny = {
            navigate(MainDirections.actionGlobalSplashFragment()) {
                setLaunchSingleTop()
            }
        }
        activityVM(ConfirmVM::class).arg.value = it
        navigate(MainDirections.actionGlobalConfirmFragment())
    }


}