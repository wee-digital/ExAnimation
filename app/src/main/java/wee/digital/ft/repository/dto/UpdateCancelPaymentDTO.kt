package wee.digital.ft.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateCancelPaymentDTOReq(
        @SerializedName("Type")
        @Expose
        var type: Int = 0
)