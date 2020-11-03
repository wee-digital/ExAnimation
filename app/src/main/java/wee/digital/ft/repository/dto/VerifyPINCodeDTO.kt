package wee.digital.ft.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PinRequest(

        @SerializedName("UID")
        var uid: List<String>,

        @SerializedName("PaymentID")
        var paymentID: String,

        @SerializedName("PinCode")
        var pinCode: String,

        @SerializedName("ClientIP")
        var clientIP: String
)

data class PinResponse(

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
        var formOtp: String = "",

        @SerializedName("HaveDefaultAccount")
        var hasDefaultAccount: Boolean,

        @SerializedName("UserID")
        var userId: String,

        )