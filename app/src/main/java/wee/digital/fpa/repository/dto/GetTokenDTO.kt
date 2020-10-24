package wee.digital.fpa.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.fpa.repository.utils.ErrCode

data class GetTokenDTOResp(
        @SerializedName("Code")
    @Expose
    var Code: Int? = ErrCode.API_FAIL,

        @SerializedName("Message")
    @Expose
    var Message: String? = "",

        @SerializedName("Token")
    @Expose
    var Token: String? = ""
)