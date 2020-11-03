package wee.digital.ft.repository.dto

import com.google.gson.annotations.SerializedName
import wee.digital.ft.repository.utils.ErrCode

data class TokenResponse(
        @SerializedName("Code")
        var Code: Int = ErrCode.API_FAIL,

        @SerializedName("Message")
        var Message: String? = "",

        @SerializedName("Token")
        var Token: String
)