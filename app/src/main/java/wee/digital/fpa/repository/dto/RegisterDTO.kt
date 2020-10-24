package wee.digital.fpa.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.fpa.repository.model.DeviceInfo

data class RegisterDTOReq(
    @SerializedName("UUID")
    @Expose
    var uid: String? = "",

    @SerializedName("PubKey")
    @Expose
    var pubKey: String? = "",

    @SerializedName("Name")
    @Expose
    var name: String? = "",
    @SerializedName("DeviceModel")
    @Expose
    var modelDevice: String? = ""
)

data class RegisterDTOResp(
    @SerializedName("Code")
    @Expose
    var code: Int = -1006,

    @SerializedName("Message")
    @Expose
    var message: String = "",

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
    var pubKey: String = ""

) {
    fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            uid = this.uid,
            fullName = this.fullName,
            shopID = this.shopID,
            shopName = this.shopName,
            posName = this.posName,
            deviceCode = this.deviceCode,
            pubKey = this.pubKey
        )
    }
}