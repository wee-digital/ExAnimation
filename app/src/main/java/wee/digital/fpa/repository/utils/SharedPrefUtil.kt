package wee.digital.fpa.repository.utils

import com.google.gson.Gson
import wee.digital.fpa.app.App
import wee.digital.fpa.repository.base.BaseSecurity
import wee.digital.fpa.repository.model.DeviceInfo

object SharedPrefUtil {
    private const val POS_DEVICE_INFO_KEY = "POS_DEVICE_INFO_KEY"
    private const val POS_PRIVATE_KEY = "POS_PRIVATE_KEY"
    private const val POS_PUBLIC_KEY = "POS_PUBLIC_KEY"
    private const val POS_LOGIN_STATUS_KEY = "POS_LOGIN_STATUS_KEY"
    private const val POS_URL_KEY = "POS_URL_KEY"
    private const val POS_ADV_KEY = "POS_ADV_KEY"
    //======
    fun clearAllData() {
        App.baseSharedPref?.removeValue(POS_DEVICE_INFO_KEY)
        App.baseSharedPref?.removeValue(POS_PRIVATE_KEY)
        App.baseSharedPref?.removeValue(POS_PUBLIC_KEY)
        App.baseSharedPref?.removeValue(POS_LOGIN_STATUS_KEY)
    }

    //Device Info
    fun saveDeviceInfo(data: String, pubkey: String){
        App.baseSharedPref?.saveStringValue(POS_DEVICE_INFO_KEY, data)
        App.baseSharedPref?.saveBooleanValue(POS_LOGIN_STATUS_KEY, true)
        //---
        this.savePubKey(BaseSecurity.ins.base64Decode(pubkey))
    }

    fun getDeviceInfo(): String{
        val dataJson = App.baseSharedPref?.getStringValue(POS_DEVICE_INFO_KEY, Gson().toJson(DeviceInfo()))
        return if(!dataJson.isNullOrEmpty()){
            dataJson
        } else {
            ""
        }
    }

    fun resetDeviceInfo(){
        App.baseSharedPref?.saveStringValue(POS_DEVICE_INFO_KEY, "")
        App.baseSharedPref?.saveBooleanValue(POS_LOGIN_STATUS_KEY, false)
    }

    //Private Key
    fun savePriKey(priKey: ByteArray){
        App.baseSharedPref?.saveStringValue(POS_PRIVATE_KEY, BaseSecurity.ins.base64Encode(priKey))
    }

    fun getPriKey():ByteArray?{
        return App.baseSharedPref?.getStringValue(POS_PRIVATE_KEY, "")?.let {
            BaseSecurity.ins.base64Decode(it)
        }
    }

    //Public Key
    private fun savePubKey(pubKey: ByteArray){
        App.baseSharedPref?.saveStringValue(POS_PUBLIC_KEY, BaseSecurity.ins.base64Encode(pubKey))
    }

    fun getPubKey():ByteArray?{
        return App.baseSharedPref?.getStringValue(POS_PUBLIC_KEY, "")?.let {
            BaseSecurity.ins.base64Decode(it)
        }
    }

    //Login Status
    fun saveLoginStatus(loginStatus: Boolean){
        App.baseSharedPref?.saveBooleanValue(POS_LOGIN_STATUS_KEY, loginStatus)
    }

    fun getLoginStatus(): Boolean {
        return App.baseSharedPref?.getBooleanValue(POS_LOGIN_STATUS_KEY, false) ?: false
    }

    //URL Type
    fun saveURLType(urlType: String){
        App.baseSharedPref?.saveStringValue(POS_URL_KEY, urlType)
    }

    fun getURLType(): String{
        return App.baseSharedPref?.getStringValue(POS_URL_KEY, "")?:""
    }

    //ADV Type
    fun saveADVType(advType: String){
        App.baseSharedPref?.saveStringValue(POS_ADV_KEY, advType)
    }

    fun getADVType(): String{
        return App.baseSharedPref?.getStringValue(POS_ADV_KEY, "")?:""
    }
}