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

    }

    private fun onFaceVerifyError(it: ConfirmArg) {
        it.onAccept = {
            v.hasFaceDetect = true
        }
        it.onDeny = {
            navigateUp()
        }
        activityVM(ConfirmVM::class).arg.value = it
        navigate(MainDirections.actionGlobalMessageFragment())
    }


}