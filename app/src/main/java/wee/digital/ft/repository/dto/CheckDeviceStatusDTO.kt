package wee.digital.ft.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CheckDeviceStatusDTOReq(
        @SerializedName("PosID")
        @Expose
        var posID: String? = "",

        @SerializedName("PubKey")
        @Expose
        var pubKey: String? = ""
)