package wee.digital.ft.ui.card

import wee.digital.ft.repository.dto.PaymentDTOReq
import wee.digital.ft.repository.dto.PaymentResponse
import wee.digital.ft.repository.network.Api
import wee.digital.ft.repository.payment.PaymentRepository
import wee.digital.ft.shared.Event
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.payment.PaymentArg

class CardVM : BaseViewModel() {

    val otpFormLiveData = EventLiveData<String>()

    val paymentSuccess = EventLiveData<Boolean>()

    val paymentFailed = EventLiveData<MessageArg>()

    fun postPayRequest(accountId: String, paymentArg: PaymentArg?) {
        paymentArg ?: throw Event.paymentArgError
        val body = PaymentDTOReq(
                paymentID = paymentArg.paymentId,
                clientIP = paymentArg.clientIp,
                accountID = accountId
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
                        paymentFailed.postValue(MessageArg.fromCode(response.code))
                    }
                }
            }

            override fun onFailed(code: Int, message: String) {
                paymentFailed.postValue(MessageArg.fromCode(0))
            }
        })
    }

}