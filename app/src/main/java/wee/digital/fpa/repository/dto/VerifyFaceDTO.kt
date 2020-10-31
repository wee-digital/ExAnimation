package wee.digital.fpa.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.fpa.repository.model.FaceInfo

data class VerifyFaceDTOReq(

        @SerializedName("Face")
        @Expose
        var face: String? = "",

        @SerializedName("PaymentID")
        @Expose
        var paymentID: String = "",

        @SerializedName("ClientIP")
        @Expose
        var clientIP: String = ""

)

data class FaceArg(
        @SerializedName("Code")
        @Expose
        var code: Int = 0,

        @SerializedName("Message")
        @Expose
        var message: String = "",

        @SerializedName("UserID")
        @Expose
        var userID: ArrayList<String> = arrayListOf(),

        @SerializedName("FullName")
        @Expose
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
) {
    fun getFaceInfo(): FaceInfo {
        return FaceInfo(
                userID = this.userID,
                fullName = this.fullName,
                isSkipPinCode = this.isSkipPinCode,
                isRetry = this.isRetry
        )
    }
}