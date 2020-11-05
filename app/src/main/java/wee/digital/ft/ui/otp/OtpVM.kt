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
