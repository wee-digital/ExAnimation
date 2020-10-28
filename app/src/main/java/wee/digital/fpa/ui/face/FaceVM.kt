package wee.digital.fpa.ui.face

import android.util.Base64
import androidx.lifecycle.ViewModel
import wee.digital.fpa.R
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.VerifyFaceDTOReq
import wee.digital.fpa.repository.dto.VerifyFaceDTOResp
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.network.CollectionData
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.arg.ConfirmArg
import wee.digital.fpa.ui.base.EventLiveData

class FaceVM : ViewModel() {

    var verifyError = EventLiveData<ConfirmArg>()

    var verifySuccess = EventLiveData<Boolean>()

    fun verifyFace(bitmap: ByteArray, dataFace: FacePointData, dataColl: DataCollect) {
        val face = Base64.encodeToString(bitmap, Base64.NO_WRAP)
        val req = VerifyFaceDTOReq(face, Shared.paymentID.value ?: "", Shared.clientID.value ?: "")

        PaymentRepository.ins.verifyFace(req, dataFace, object : Api.ClientListener<VerifyFaceDTOResp> {
            override fun onSuccess(data: VerifyFaceDTOResp) {
                CollectionData.instance.encryptCollData(dataColl)
                onVerifyFaceSuccess()
            }

            override fun onFailed(code: Int, message: String) {
                onVerifyFaceFailed()
            }

        })
    }

    fun onVerifyFaceSuccess() {
        verifySuccess.postValue(true)
    }

    fun onVerifyFaceFailed() {
        val message = ConfirmArg(
                headerGuideline = R.id.guidelineFace,
                icon = R.mipmap.img_x_mark_flat,
                title = "Tài khoản không tồn tại",
                message = "Bạn vui lòng đăng ký tài khoản Facepay trước khi thực hiện thanh toán",
                buttonAccept = "Thử lại",
                buttonDeny = "Hủy bỏ giao dịch")
        verifyError.postValue(message)
    }

}