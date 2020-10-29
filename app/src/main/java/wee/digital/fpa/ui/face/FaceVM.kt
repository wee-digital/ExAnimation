package wee.digital.fpa.ui.face

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wee.digital.fpa.R
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.data.local.Config
import wee.digital.fpa.repository.dto.VerifyFaceDTOReq
import wee.digital.fpa.repository.dto.VerifyFaceDTOResp
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.network.CollectionData
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.payment.PaymentArg
import java.util.concurrent.atomic.AtomicInteger

class FaceVM : ViewModel() {

    var faceArg = MutableLiveData<VerifyFaceDTOResp?>()

    var verifyRetry = EventLiveData<ConfirmArg>()

    var verifyError = EventLiveData<MessageArg>()

    private val retryCount = AtomicInteger(Config.FACE_RETRY_COUNT)

    fun verifyFace(bitmap: ByteArray,
                   dataFace: FacePointData,
                   dataColl: DataCollect,
                   paymentArg: PaymentArg?
    ) {
        paymentArg ?: throw Event.paymentArgError
        retryCount.decrementAndGet()
        val face = Base64.encodeToString(bitmap, Base64.NO_WRAP)
        val req = VerifyFaceDTOReq(face, paymentArg.paymentId, paymentArg.clientIp)

        PaymentRepository.ins.verifyFace(req, dataFace, object : Api.ClientListener<VerifyFaceDTOResp> {
            override fun onSuccess(data: VerifyFaceDTOResp) {
                CollectionData.instance.encryptCollData(dataColl)
                onVerifyFaceSuccess(data)
            }

            override fun onFailed(code: Int, message: String) {
                onVerifyFaceFailed()
            }

        })
    }

    fun onVerifyFaceSuccess(data: VerifyFaceDTOResp) {
        faceArg.postValue(data)
    }

    fun onVerifyFaceFailed() {
        when (retryCount.decrementAndGet()) {
            0 -> {
                verifyError.postValue(MessageArg.paymentCancelMessage)
            }
            else -> verifyRetry.postValue(ConfirmArg(
                    headerGuideline = R.id.guidelineFace,
                    icon = R.mipmap.img_x_mark_flat,
                    title = "Tài khoản không tồn tại",
                    message = "Bạn vui lòng đăng ký tài khoản Facepay trước khi thực hiện thanh toán",
                    buttonAccept = "Thử lại",
                    buttonDeny = "Hủy bỏ giao dịch"
            ))
        }

    }

}