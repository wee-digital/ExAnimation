package wee.digital.fpa.ui.face

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.confirm.ConfirmVM

class FaceFragment : BaseFragment() {

    private val vm: FaceVM by lazy { activityVM(FaceVM::class) }

    private val v: FaceView by lazy { FaceView(this) }

    override fun layoutResource(): Int = R.layout.face

    override fun onViewCreated() {
        v.onViewInit()
        v.startRemaining {
            navigate(MainDirections.actionGlobalSplashFragment()) {
                setLaunchSingleTop()
            }
        }
        v.onFaceEligible = { bitmap, pointData, dataCollect ->
            vm.verifyFace(bitmap, pointData, dataCollect)
        }
    }

    override fun onLiveDataObserve() {
        vm.verifyError.observe {
            onFaceVerifyError(it)
        }
        vm.verifySuccess.observe {
            onFaceVerifySuccess()
        }
    }

    private fun onFaceVerifySuccess() {
        v.animateOnFaceCaptured()
        navigate(MainDirections.actionGlobalPinFragment())
    }

    private fun onFaceVerifyError(it: ConfirmArg) {
        onFaceVerifySuccess()
        return
        v.animateOnFaceCaptured()
        it.onAccept = {
            v.animateOnStartFaceReg {
                v.hasFaceDetect = true
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