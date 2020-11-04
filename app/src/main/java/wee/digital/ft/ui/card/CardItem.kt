package wee.digital.ft.ui.card

import wee.digital.ft.shared.Shared
import wee.digital.ft.repository.dto.CardListResponse
import wee.digital.ft.shared.Event
import wee.digital.library.extension.listString
import wee.digital.library.extension.str
import wee.digital.library.extension.toObject

data class CardItem(
        val accountId: String,
        val bankCode: String,
        val name: String,
        val shortName: String,
        val colors: List<String>
) {

    companion object {
        fun getList(data: CardListResponse): List<CardItem> {
            val banks = Shared.bankJson.value ?: throw Event.bankDataError
            val accBanks = data.basicBankAccList
            val cardList = mutableListOf<CardItem>()
            for (i in 0 until banks.size()) {
                for (j in 0..accBanks.lastIndex) {
                    val obj = banks.get(i).toObject() ?: continue
                    val accBanks = accBanks[j]
                    if (obj.str("BankCode") != accBanks.BankCode) continue

                    val item = CardItem(
                            accountId = accBanks.id,
                            bankCode = obj.str("BankCode") ?: continue,
                            name = obj.str("BankName") ?: continue,
                            shortName = obj.str("BankShortName") ?: continue,
                            colors = obj.listString("BackgroundColors")
                                    ?: listOf("#F58220", "#FF9C48"),
                    )
                    cardList.add(item)
                    break
                }
            }
            return cardList
        }
    }
}