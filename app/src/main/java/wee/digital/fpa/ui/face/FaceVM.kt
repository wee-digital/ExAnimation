package wee.digital.fpa.ui.face

import android.util.Base64
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
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.payment.PaymentArg
import java.util.concurrent.atomic.AtomicInteger

class FaceVM : BaseViewModel() {

    private val retryCount = AtomicInteger(Config.FACE_RETRY_COUNT)
    val successLiveData = EventLiveData<FaceArg>()
    val failureLiveData = EventLiveData<Boolean>()
    val retriesLiveData = EventLiveData<Boolean>()

    fun verifyFace(bitmap: ByteArray, dataFace: FacePointData, dataColl: DataCollect, paymentArg: PaymentArg?) {
        paymentArg ?: throw Event.paymentArgError
        val face = Base64.encodeToString(bitmap, Base64.NO_WRAP)
        val req = FaceRequest(face, paymentArg.paymentId, paymentArg.clientIp)
        PaymentRepository.ins.verifyFace(req, dataFace, object : Api.ClientListener<FaceResponse> {
            override fun onSuccess(response: FaceResponse) {
                CollectionData.instance.encryptCollData(dataColl)
                successLiveData.postValue(FaceArg(response))
            }

            override fun onFailed(code: Int, message: String) {
                when (retryCount.getAndDecrement()) {
                    0 -> {
                        failureLiveData.postValue(true)
                    }
                    else -> {
                        retriesLiveData.postValue(true)
                    }
                }
            }

        })
    }

}