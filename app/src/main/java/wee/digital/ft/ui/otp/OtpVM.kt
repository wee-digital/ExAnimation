package wee.digital.ft.ui.otp

import wee.digital.ft.repository.dto.CardListResponse
import wee.digital.ft.repository.dto.GetBankAccListDTOReq
import wee.digital.ft.repository.network.Api
import wee.digital.ft.repository.payment.PaymentRepository
import wee.digital.ft.shared.Event
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
import wee.digital.ft.ui.card.CardItem
import wee.digital.ft.ui.confirm.ConfirmArg
import wee.digital.ft.ui.message.MessageArg
import java.util.concurrent.atomic.AtomicInteger

class OtpVM : BaseViewModel() {

    private val retryCount = AtomicInteger(1)

    val cardList = EventLiveData<List<CardItem>>()

    val retryMessageLiveData = EventLiveData<ConfirmArg>()

    val errorMessageLiveData = EventLiveData<MessageArg>()

    fun onTransactionFailed(data: String) {
        if (retryCount.getAndDecrement() > 0) {
            onPaymentRetry(data)
        } else {
            onPaymentError(data)
        }

    }

    private fun onPaymentRetry(data: String) {
        when (data) {
            Napas.INSUFFICIENT_FUNDS -> {
                retryMessageLiveData.postValue(ConfirmArg(
                        title = "Giao dịch thất bại",
                        message = "Không đủ số dư thanh toán. Bạn vui lòng chọn thẻ khác"
                ))
            }
            Napas.BELOW_LIMIT, Napas.OUT_OF_LIMIT_BANK -> {
                retryMessageLiveData.postValue(ConfirmArg(
                        title = "Giao dịch thất bại",
                        message = "Quá hạn mức giao dịch. Bạn vui lòng chọn thẻ khác"
                ))
            }
            else -> {
                errorMessageLiveData.postValue(MessageArg.paymentCancel)
            }
        }
    }

    private fun onPaymentError(data: String) {
        when (data) {
            Napas.INSUFFICIENT_FUNDS -> {
                errorMessageLiveData.postValue(MessageArg(
                        title = "Giao dịch thất bại",
                        message = "Quá hạn mức giao dịch.",
                        button = null
                ))
            }
            Napas.BELOW_LIMIT, Napas.OUT_OF_LIMIT_BANK -> {
                errorMessageLiveData.postValue(MessageArg(
                        title = "Giao dịch thất bại",
                        message = "Quá hạn mức giao dịch.",
                        button = null
                ))
            }
            else -> {
                errorMessageLiveData.postValue(MessageArg.paymentCancel)
            }
        }
    }

    fun fetchCardList(userId: String?) {
        val body = GetBankAccListDTOReq(
                userID = userId ?: throw Event.pinDataError
        )
        PaymentRepository.ins.getBankAccList(body, object : Api.ClientListener<CardListResponse> {
            override fun onSuccess(response: CardListResponse) {
                cardList.postValue(CardItem.getList(response))
            }

            override fun onFailed(code: Int, message: String) {
                cardList.postValue(null)
            }

        })
    }
}
