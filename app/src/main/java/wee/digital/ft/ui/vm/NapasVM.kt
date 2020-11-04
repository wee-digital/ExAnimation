package wee.digital.ft.ui.vm

import wee.digital.ft.shared.Shared
import wee.digital.ft.repository.dto.SocketResponse
import wee.digital.ft.repository.dto.UpdateCancelPaymentDTOReq
import wee.digital.ft.repository.dto.UpdatePaymentStatusDTOReq
import wee.digital.ft.repository.model.ClientResponse
import wee.digital.ft.repository.network.Api
import wee.digital.ft.repository.payment.PaymentRepository
import wee.digital.ft.repository.utils.PaymentStatusCode
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
import wee.digital.ft.ui.payment.PaymentArg

class NapasVM : BaseViewModel() {

    val paymentLiveData = EventLiveData<PaymentArg>()

    fun getNapasClient(socketRes: SocketResponse) {
        PaymentRepository.ins.getClientId(object : Api.ClientListener<ClientResponse> {

            override fun onSuccess(response: ClientResponse) {
                log.d("getNapasClient: $response")
                if (Shared.paymentProcessing) {
                    updateStatusPayment(socketRes.paymentId, PaymentStatusCode.DEVICE_PROCESSING)
                    Shared.paymentProcessing = true
                    return
                }
                paymentLiveData.postValue(PaymentArg(socketRes, response))
            }

            override fun onFailed(code: Int, message: String) {
                log.d("getNapasClient: $code $message")
                paymentLiveData.postValue(null)
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