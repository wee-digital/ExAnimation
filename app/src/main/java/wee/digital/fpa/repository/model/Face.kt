package wee.digital.fpa.repository.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FaceInfo(
        @SerializedName("UserID")
        @Expose
        var userID: String? = "",

        @SerializedName("FullName")
        @Expose
        var fullName: String? = "",

        @SerializedName("IsSkipPinCode")
        @Expose
        var isSkipPinCode: Boolean = false,

        @SerializedName("ReTry")
        @Expose
        var isRetry: Boolean = false
)

data class FacePayInfo(
        var userID: String = "",
        var fullName: String = "",
        var facepayNumber: String = "",
        var maskedAccount: String = "",
        var bankName: String = "",
        var amount: Int = 0,
        var isRetry: Boolean = false
)