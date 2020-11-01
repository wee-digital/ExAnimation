package wee.digital.fpa.repository.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClientResponse(
        @SerializedName("IP")
        @Expose
        var ip: String = ""
)

