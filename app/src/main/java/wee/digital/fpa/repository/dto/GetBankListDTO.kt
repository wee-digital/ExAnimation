package wee.digital.fpa.repository.dto

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.fpa.app.App
import wee.digital.fpa.repository.model.BankAccInfo
import wee.digital.fpa.repository.model.BasicBankAcc
import wee.digital.fpa.repository.utils.ErrCode
import wee.digital.library.extension.readAsset

data class GetBankAccListDTOReq(
    @SerializedName("UserID")
    @Expose
    var userID: String = ""
)

data class GetBankAccListDTOResp(
        @SerializedName("Code")
    @Expose
    var code: Int = ErrCode.API_FAIL,

        @SerializedName("Message")
    @Expose
    var message: String? = "",

        @SerializedName("BankAccounts")
    @Expose
    var basicBankAccList: List<BasicBankAcc> = arrayListOf()
){
    fun getBankAccList(): ArrayList<BankAccInfo>{
        // dev lai
        /*val bankAccList = arrayListOf<BankAccInfo>()
        if (this.basicBankAccList.isNotEmpty()) {
            val jsonArray =
                if (App.banksJson == null) readAsset("bank_list.json").toArray() else MyApp.banksJson
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
        return bankAccList*/
    }
}