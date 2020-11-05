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

    val cardList = EventLiveData<List<CardItem>>()

    val errorMessageLiveData = EventLiveData<MessageArg>()

    fun onTransactionFailed(data: String? = null) {
        when (data) {
            Napas.INSUFFICIENT_FUNDS -> {
                errorMessageLiveData.postValue(MessageArg.insufficient)
            }
            Napas.BELOW_LIMIT, Napas.OUT_OF_LIMIT_BANK -> {
                errorMessageLiveData.postValue(MessageArg.paymentLimitError)
            }
            else -> {
                errorMessageLiveData.postValue(MessageArg.paymentError)
            }
        }
    }


    fun fetchCardList(userId: String?) {
        val body = GetBankAccListDTOReq(
                userID = userId ?: throw Event.pinDataError
        )
        PaymentRepository.ins.getBankAccList(body, object : Api.ClientListener<CardListResponse> {
            override fun onSuccess(response: CardListResponse) {
                val cards = CardItem.getList(response)
                cardList.postValue(cards)
            }

            override fun onFailed(code: Int, message: String) {
                errorMessageLiveData.postValue(MessageArg.paymentError)
            }

        })
    }
}
