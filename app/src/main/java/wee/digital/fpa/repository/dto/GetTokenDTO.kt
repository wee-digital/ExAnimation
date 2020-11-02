package wee.digital.fpa.repository.dto

import com.google.gson.annotations.SerializedName
import wee.digital.fpa.repository.utils.ErrCode

data class TokenResponse(
        @SerializedName("Code")
        var Code: Int = ErrCode.API_FAIL,

        @SerializedName("Message")
        var Message: String? = "",

        @SerializedName("Token")
        var Token: String
)