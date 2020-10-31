package wee.digital.fpa.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.fpa.repository.model.FaceInfo

data class FaceRequest(

        @SerializedName("Face")
        var face: String,

        @SerializedName("PaymentID")
        var paymentID: String,

        @SerializedName("ClientIP")
        var clientIP: String
)

data class FaceResponse(
        @SerializedName("Code")
        @Expose
        var code: Int = 0,

        @SerializedName("Message")
        @Expose
        var message: String = "",

        @SerializedName("UserID")
        @Expose
        var userID: List<String> = listOf(),

        var fullName: String? = "",

        @SerializedName("IsSkipPinCode")
        @Expose
        var isSkipPinCode: Boolean = false,

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