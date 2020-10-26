package wee.digital.fpa.ui.plash

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.library.extension.post

class SplashVM : BaseViewModel() {

    val paymentInfo = MutableLiveData<JsonObject?>()

    val hasDeviceInfo = EventLiveData<Boolean>()

    fun checkLocalDeviceInfo() {
        post(1000) {
            val info = BaseData.ins.getDeviceInfoPref()
            log.d("checkLocalDeviceInfo - uid: ${info.uid}")
            hasDeviceInfo.postValue(!info.uid.isNullOrEmpty())
        }
    }
}