package wee.digital.fpa.ui.face

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.data.local.Config
import wee.digital.fpa.repository.dto.FaceRequest
import wee.digital.fpa.repository.dto.FaceResponse
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.network.CollectionData
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.payment.PaymentArg
import java.util.concurrent.atomic.AtomicInteger

class FaceVM : BaseViewModel() {

    private val retryCount = AtomicInteger()

    var arg = MutableLiveData<FaceArg?>()

    override fun onStart() {
        retryCount.set(Config.FACE_RETRY_COUNT)
    }

    fun verifyFace(bitmap: ByteArray, dataFace: FacePointData, dataColl: DataCollect, paymentArg: PaymentArg?) {
        paymentArg ?: throw Event.paymentArgError
        val face = Base64.encodeToString(bitmap, Base64.NO_WRAP)
        val req = FaceRequest(face, paymentArg.paymentId, paymentArg.clientIp)
        PaymentRepository.ins.verifyFace(req, dataFace, object : Api.ClientListener<FaceResponse> {
            override fun onSuccess(response: FaceResponse) {
                CollectionData.instance.encryptCollData(dataColl)
                onVerifyFaceSuccess(response)
            }

            override fun onFailed(code: Int, message: String) {
                onVerifyFaceFailed()
            }

        })
    }

    fun onVerifyFaceSuccess(response: FaceResponse) {
        arg.postValue(FaceArg(response))
        eventLiveData.postValue(FaceEvent.VERIFY_SUCCESS)
    }

    fun onVerifyFaceFailed() {
        when (retryCount.getAndDecrement()) {
            0 -> {
                eventLiveData.postValue(FaceEvent.VERIFY_FAILED)
            }
            else -> {
                eventLiveData.postValue(FaceEvent.VERIFY_RETRY)
            }
        }

    }

}