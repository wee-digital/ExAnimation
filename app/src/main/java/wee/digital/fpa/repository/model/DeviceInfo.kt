package wee.digital.fpa.repository.model

import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DeviceInfo(
        @SerializedName("UID")
        @Expose
        var uid: String = "",

        @SerializedName("FullName")
        @Expose
        var fullName: String = "",

        @SerializedName("ShopName")
        @Expose
        var shopName: String = "",

        @SerializedName("ShopID")
        @Expose
        var shopID: String = "",

        @SerializedName("PosName")
        @Expose
        var posName: String = "",

        @SerializedName("DeviceCode")
        @Expose
        var deviceCode: String = "",

        @SerializedName("PubKey")
        @Expose
        var pubKey: String = "",

        @Ignore
        var qrCode: String = "",

        @Ignore
        var name: String = ""
)

class DeviceInfoStore(
        val qrCode: String,
        val name: String
)