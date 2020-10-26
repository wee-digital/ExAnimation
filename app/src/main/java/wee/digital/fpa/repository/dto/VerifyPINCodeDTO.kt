package wee.digital.fpa.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VerifyPINCodeDTOReq(

        @SerializedName("UID")
        @Expose
        var uid: ArrayList<String> = arrayListOf(),

        @SerializedName("PaymentID")
        @Expose
        var paymentID: String,

        @SerializedName("PinCode")
        @Expose
        var pinCode: String,

        @SerializedName("ClientIP")
        @Expose
        var clientID: String = ""

)

data class VerifyPINCodeDTOResp(

        @SerializedName("Code")
        @Expose
        var code: Int = -1006,

        @SerializedName("Message")
        @Expose
        var message: String = "",

        @SerializedName("ReTry")
        @Expose
        var isRetry: Boolean = false,

        @SerializedName("HaveForm")
        @Expose
        var haveOTP: Boolean = false,

        @SerializedName("Form")
        @Expose
        var formOtp: String = ""

)