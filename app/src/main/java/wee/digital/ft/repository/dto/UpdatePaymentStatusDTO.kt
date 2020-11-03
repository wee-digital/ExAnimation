package wee.digital.ft.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdatePaymentStatusDTOReq(
        @SerializedName("PaymentID")
        @Expose
        var paymentID: String? = "",

        @SerializedName("Status")
        @Expose
        var status: Int = 0

)