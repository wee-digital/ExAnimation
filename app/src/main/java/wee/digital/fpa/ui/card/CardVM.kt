package wee.digital.fpa.ui.card

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.GetBankAccListDTOReq
import wee.digital.fpa.repository.dto.GetBankAccListDTOResp
import wee.digital.fpa.repository.dto.PinArg
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.library.extension.listString
import wee.digital.library.extension.str
import wee.digital.library.extension.toObject

class CardVM : BaseViewModel() {

    val cardList = MutableLiveData<List<CardItem>>()

    fun fetchCardList(pinArg: PinArg?) {
        val body = GetBankAccListDTOReq(
                userID = pinArg?.userId ?: throw Event.pinDataError
        )
        PaymentRepository.ins.getBankAccList(body, object : Api.ClientListener<GetBankAccListDTOResp> {
            override fun onSuccess(data: GetBankAccListDTOResp) {
                onCardListResponse(data)
            }

            override fun onFailed(code: Int, message: String) {

            }

        })
    }

    private fun onCardListResponse(data: GetBankAccListDTOResp) {
        val banks = Shared.bankJson.value ?: throw Event.bankDataError
        val accBanks = data.basicBankAccList
        val cardItems = mutableListOf<CardItem>()
        for (i in 0 until banks.size()) {
            for (j in 0..accBanks.lastIndex) {
                val obj = banks.get(i).toObject() ?: continue
                val accBanks = accBanks[j]
                if (obj.str("BankCode") != accBanks.BankCode) continue
                val item = CardItem(
                        id = accBanks.id,
                        bankCode = obj.str("BankCode") ?: continue,
                        name = obj.str("BankName") ?: continue,
                        shortName = obj.str("BankShortName") ?: continue,
                        colors = obj.listString("BackgroundColors") ?: listOf("#F58220", "#FF9C48"),
                )
                cardItems.add(item)
                break
            }
        }
        cardList.postValue(cardItems)
    }


}