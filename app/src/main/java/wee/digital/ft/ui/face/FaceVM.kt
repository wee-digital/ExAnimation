package wee.digital.ft.ui.face

import android.util.Base64
import wee.digital.ft.camera.DataCollect
import wee.digital.ft.camera.FacePointData
import wee.digital.ft.repository.dto.FaceRequest
import wee.digital.ft.repository.dto.FaceResponse
import wee.digital.ft.repository.network.Api
import wee.digital.ft.repository.network.CollectionData
import wee.digital.ft.repository.payment.PaymentRepository
import wee.digital.ft.shared.Config
import wee.digital.ft.shared.Event
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.payment.PaymentArg
import wee.digital.library.extension.post
import java.util.concurrent.atomic.AtomicInteger

class FaceVM : BaseViewModel() {

    private val retryCount = AtomicInteger(Config.FACE_RETRY_COUNT)

    val successLiveData = EventLiveData<FaceArg>()

    val failureLiveData = EventLiveData<MessageArg>()

    val retriesLiveData = EventLiveData<Boolean>()

    fun verifyFace(bitmap: ByteArray, dataFace: FacePointData, dataColl: DataCollect, paymentArg: PaymentArg?) {
        if (Config.TESTING) post(2000) {
            successLiveData.value = FaceArg.testArg
            return@post
        }
        paymentArg ?: throw Event.paymentArgError
        val face = Base64.encodeToString(bitmap, Base64.NO_WRAP)
        val req = FaceRequest(face, paymentArg.paymentId, paymentArg.clientIp)
        PaymentRepository.ins.verifyFace(req, dataFace, object : Api.ClientListener<FaceResponse> {
            override fun onSuccess(response: FaceResponse) {
                CollectionData.instance.encryptCollData(dataColl)
                when (response.code) {
                    0 -> {
                        successLiveData.postValue(FaceArg(response))
                    }
                    else -> {
                        onVerifyFailed()
                    }
                }
            }

            override fun onFailed(code: Int, message: String) {
                onVerifyFailed()
            }
        })
    }

    fun onVerifyFailed() {
        when (retryCount.getAndDecrement()) {
            0 -> {
                failureLiveData.postValue(MessageArg.faceNotExistedError)
            }
            else -> {
                retriesLiveData.postValue(true)
            }
        }
    }

}