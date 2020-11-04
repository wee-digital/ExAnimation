package wee.digital.ft.ui.otp

import wee.digital.ft.repository.dto.CardListResponse
import wee.digital.ft.repository.dto.GetBankAccListDTOReq
import wee.digital.ft.repository.network.Api
import wee.digital.ft.repository.payment.PaymentRepository
import wee.digital.ft.shared.Event
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
import wee.digital.ft.ui.card.CardItem
import wee.digital.ft.ui.message.MessageArg
import java.util.concurrent.atomic.AtomicInteger

class OtpVM : BaseViewModel() {

    private val retryCount = AtomicInteger(1)

    val cardList = EventLiveData<List<CardItem>>()

    val retryMessageLiveData = EventLiveData<MessageArg>()

    val errorMessageLiveData = EventLiveData<MessageArg>()

    fun onTransactionFailed(data: String) {
        val liveData = if (retryCount.getAndDecrement() > 0) {
            retryMessageLiveData
        } else {
            errorMessageLiveData
        }
        when (data) {
            Napas.INSUFFICIENT_FUNDS -> {
                liveData.postValue(MessageArg.insufficient)
            }
            Napas.BELOW_LIMIT, Napas.OUT_OF_LIMIT_BANK -> {
                liveData.postValue(MessageArg.paymentLimitError)
            }
            else -> {
                liveData.postValue(MessageArg.paymentError)
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
