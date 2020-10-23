package wee.digital.fpa.repository.base

import com.google.gson.Gson
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.repository.utils.SharedPrefUtil
import java.lang.Exception

class BaseData {
    private var mDeviceInfo: DeviceInfo = DeviceInfo()
    //---
    companion object {
        val ins: BaseData by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BaseData()
        }

        var deviceInfo: DeviceInfo
            get() = Gson().fromJson(SharedPrefUtil.getDeviceInfo(), DeviceInfo::class.java)
            set(value){
                ins.mDeviceInfo = value
            }

        var loginStatus: Boolean
            get() = SharedPrefUtil.getLoginStatus()
            set(value) {
                SharedPrefUtil.saveLoginStatus(loginStatus)
            }
    }

    //==DEVICE INFO====
    fun saveDeviceInfoPref(deviceInfo: DeviceInfo?, pubkey: String){
        if(deviceInfo != null){
            val dataJson = Gson().toJson(deviceInfo)
            SharedPrefUtil.saveDeviceInfo(data = dataJson, pubkey = pubkey)
            //---
            mDeviceInfo = deviceInfo
        }
    }

    fun getDeviceInfoPref():DeviceInfo{
        try{
            val dataJson = SharedPrefUtil.getDeviceInfo()
            if(dataJson.isEmpty()){
                return DeviceInfo()
            }
            //---
            return Gson().fromJson(dataJson, DeviceInfo::class.java)
        } catch (ex: Exception){
            return DeviceInfo()
        }
    }

    fun resetDeviceInfo(){
        SharedPrefUtil.clearAllData()
//        RoomHelper.instance.appDao.clear() dev lai
        mDeviceInfo = DeviceInfo()
    }
}