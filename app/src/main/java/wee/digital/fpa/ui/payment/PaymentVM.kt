package wee.digital.fpa.ui.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.SocketResultResp
import wee.digital.fpa.repository.dto.UpdateCancelPaymentDTOReq
import wee.digital.fpa.repository.dto.UpdatePaymentStatusDTOReq
import wee.digital.fpa.repository.model.ClientIDResp
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.repository.utils.PaymentStatusCode
import wee.digital.fpa.ui.base.BaseViewModel

class PaymentVM : BaseViewModel(){

    val paymentArg = MutableLiveData<PaymentArg?>()

    fun getNapasClient(dataSocket: SocketResultResp) {
        PaymentRepository.ins.getClientId(object : Api.ClientListener<ClientIDResp> {

            override fun onSuccess(data: ClientIDResp) {
                log.d("getNapasClient: $data")
                if (Shared.paymentProcessing) {
                    updateStatusPayment(dataSocket.paymentId, PaymentStatusCode.DEVICE_PROCESSING)
                    Shared.paymentProcessing = true
                    return
                }
                paymentArg.postValue(PaymentArg(
                        clientIp = data.ip,
                        paymentId = dataSocket.paymentId,
                        amount = dataSocket.amount,
                        timeout = dataSocket.timeOut
                ))
            }

            override fun onFailed(code: Int, message: String) {
                log.d("getNapasClient: $code $message")
                paymentArg.postValue(null)
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