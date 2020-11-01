package wee.digital.fpa.ui.card

import wee.digital.fpa.repository.dto.PaymentDTOReq
import wee.digital.fpa.repository.dto.PaymentResponse
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.payment.PaymentArg

class CardVM : BaseViewModel() {

    val otpFormLiveData = EventLiveData<String>()

    val paymentSuccess = EventLiveData<Boolean>()

    val paymentFailed = EventLiveData<Boolean>()

    fun postPayRequest(bankCode: String, paymentArg: PaymentArg?) {
        paymentArg ?: throw Event.paymentArgError
        val body = PaymentDTOReq(
                paymentID = paymentArg.paymentId,
                clientIP = paymentArg.clientIp,
                accountID = bankCode
        )
        PaymentRepository.ins.payment(body, object : Api.ClientListener<PaymentResponse> {
            override fun onSuccess(response: PaymentResponse) {
                when {
                    response.code == 0 -> {
                        paymentSuccess.postValue(true)
                    }
                    response.haveOTP && !response.formOtp.isNullOrEmpty() -> {
                        otpFormLiveData.postValue(response.formOtp)
                    }
                    else -> {
                        paymentFailed.postValue(true)
                    }
                }
            }
        })
    }

}