package wee.digital.fpa.repository.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.fpa.repository.dto.SocketResultResp

data class ClientIDResp(
        @SerializedName("IP")
        @Expose
        var ip: String = ""
)

