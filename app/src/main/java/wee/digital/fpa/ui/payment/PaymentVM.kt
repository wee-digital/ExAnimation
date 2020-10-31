package wee.digital.fpa.ui.payment

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.SocketResponse
import wee.digital.fpa.repository.dto.UpdateCancelPaymentDTOReq
import wee.digital.fpa.repository.dto.UpdatePaymentStatusDTOReq
import wee.digital.fpa.repository.model.ClientResponse
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.repository.utils.PaymentStatusCode
import wee.digital.fpa.ui.base.BaseViewModel

class PaymentVM : BaseViewModel() {

    val arg = MutableLiveData<PaymentArg?>()

    override fun onStart() {
    }

    fun getNapasClient(socketRes: SocketResponse) {
        PaymentRepository.ins.getClientId(object : Api.ClientListener<ClientResponse> {

            override fun onSuccess(response: ClientResponse) {
                log.d("getNapasClient: $response")
                if (Shared.paymentProcessing) {
                    updateStatusPayment(socketRes.paymentId, PaymentStatusCode.DEVICE_PROCESSING)
                    Shared.paymentProcessing = true
                    return
                }
                arg.postValue(PaymentArg(socketRes, response))
            }

            override fun onFailed(code: Int, message: String) {
                log.d("getNapasClient: $code $message")
                arg.postValue(null)
            }

        })
    }

    fun updateStatusPayment(id: String, status: Int) {
        val dataReq = UpdatePaymentStatusDTOReq(paymentID = id, status = status)
        PaymentRepository.ins.updatePaymentStatus(dataReq)
    }

    fun requestCancelPayment(type: Int) {
        val dataReq = UpdateCancelPaymentDTOReq(type = type)
        PaymentRepository.ins.updateCancelPayment(dataReq)
    }
}