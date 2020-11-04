package wee.digital.ft.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.ft.repository.model.FacePayInfo

data class RequestPaymentDTOReq(
        @SerializedName("Amount")
        @Expose
        val amount: Int = 0
)

data class RequestPaymentDTOResp(
        @SerializedName("Code")
        @Expose
        val code: Int = 0,

        @SerializedName("Message")
        @Expose
        val message: String = "",

        @SerializedName("PaymentID")
        @Expose
        val paymentID: String = "",

        @SerializedName("PaymentRequestTimeout")
        @Expose
        val timeOut: Int = 0

)

data class PaymentDTOReq(

        @SerializedName("PaymentID")
        @Expose
        var paymentID: String = "",

        @SerializedName("AccountID")
        @Expose
        var accountID: String? = null,

        @SerializedName("ClientIP")
        @Expose
        var clientIP: String = ""

)

data class PaymentResponse(

        @SerializedName("Code")
        @Expose
        var code: Int = -1006,

        @SerializedName("Message")
        @Expose
        var message: String = "",

        @SerializedName("UserID")
        @Expose
        var userID: String = "",

        @SerializedName("FullName")
        @Expose
        var fullName: String = "",

        @SerializedName("FacepayNumber")
        @Expose
        var facepayNumber: String = "",

        @SerializedName("MaskedAccount")
        @Expose
        var maskedAccount: String = "",

        @SerializedName("BankName")
        @Expose
        var bankName: String = "",

        @SerializedName("Amount")
        @Expose
        var amount: Int = 0,

        @SerializedName("TransactionID")
        @Expose
        var transactionID: String = "",

        @SerializedName("ReTry")
        @Expose
        var isRetry: Boolean = false,

        // TODO: pin fragment arg to otp fragment
        // [haveOTP] = true
        @SerializedName("HaveForm")
        @Expose
        var haveOTP: Boolean = false,

        // TODO: pin fragment arg to otp fragment
        // [haveOTP] = true
        @SerializedName("Form")
        @Expose
        var otpForm: String = ""
) {
    fun getFacePayInfo(): FacePayInfo {
        return FacePayInfo(
                userID = this.userID,
                fullName = this.fullName,
                facepayNumber = this.facepayNumber,
                maskedAccount = this.maskedAccount,
                bankName = this.bankName,
                amount = this.amount,
                isRetry = this.isRetry


        )
    }
}