package wee.digital.fpa.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VideoRecordReq(
        @SerializedName("Face")
        @Expose
        var face: String = "",

        @SerializedName("Data")
        @Expose
        var data: String = ""
)

data class VideoRecordData(
        @SerializedName("Face")
        @Expose
        var face: ByteArray? = null,

        @SerializedName("Data")
        @Expose
        var data: InfoVideoReq? = InfoVideoReq()
)

data class InfoVideoReq(
        @SerializedName("PaymentID")
        @Expose
        var paymentID: String? = "",

        @SerializedName("TransactionID")
        @Expose
        var transactionID: String? = "",

        @SerializedName("HashedFace")
        @Expose
        var hashedFace: String = ""
)