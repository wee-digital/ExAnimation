package wee.digital.fpa.repository.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BasicBankAcc(
        @SerializedName("ID")
        @Expose
        var id: String = "",

        @SerializedName("BankCode")
        @Expose
        var BankCode: String = ""
)

data class  BankAccInfo(
        @SerializedName("BankCode")
        @Expose
        var BankCode: String = "",

        @SerializedName("BankName")
        @Expose
        var BankName: String = "",

        @SerializedName("Descriptions")
        @Expose
        var descriptions: String = "",

        @SerializedName("BankShortName")
        @Expose
        var BankShortName: String = "",

        @SerializedName("OptionConnect")
        @Expose
        var optionConnect: String = "",

        @SerializedName("Advertising")
        @Expose
        var advertising: String = "",

        @SerializedName("LinkedFacepay")
        @Expose
        var linkedFacepay: Boolean = false,

        @SerializedName("ID")
        @Expose
        var accountID: String = ""
)
