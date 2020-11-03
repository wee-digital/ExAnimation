package wee.digital.ft.repository.dto

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.ft.data.repository.Shared
import wee.digital.ft.repository.model.BankAccInfo
import wee.digital.ft.repository.model.BasicBankAcc
import wee.digital.ft.repository.utils.ErrCode
import wee.digital.library.extension.readAsset
import wee.digital.library.extension.toArray

data class GetBankAccListDTOReq(
        @SerializedName("UserID")
        @Expose
        var userID: String = ""
)

data class CardListResponse(
        @SerializedName("Code")
        @Expose
        var code: Int = ErrCode.API_FAIL,

        @SerializedName("Message")
        @Expose
        var message: String? = "",

        @SerializedName("BankAccounts")
        @Expose
        var basicBankAccList: List<BasicBankAcc> = arrayListOf()
) {
    fun getBankAccList(): ArrayList<BankAccInfo> {
        val bankAccList = arrayListOf<BankAccInfo>()
        if (this.basicBankAccList.isNotEmpty()) {
            val jsonArray =
                    Shared.bankJson.value ?: readAsset("bank_list.json").toArray()
            val bankListJson = Gson().fromJson(jsonArray, Array<BankAccInfo>::class.java)

            for (basicBankAcc in this.basicBankAccList) {
                val bankAccInfo =
                        bankListJson.single { item ->
                            item.BankCode == basicBankAcc.BankCode
                        }

                bankAccInfo.accountID = basicBankAcc.id
                bankAccList.add(bankAccInfo)
            }
        }
        return bankAccList
    }
}