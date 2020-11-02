package wee.digital.fpa.ui.device

import com.google.gson.JsonObject
import wee.digital.fpa.data.local.Event
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData

class DeviceVM : BaseViewModel() {

    val nameErrorLiveData = EventLiveData<String>()

    val successLiveData = EventLiveData<Boolean>()

    val failureLiveData = EventLiveData<Boolean>()

    fun registerDevice(sName: String?, qr: JsonObject?) {
        if (sName?.length ?: 0 < 5) {
            nameErrorLiveData.postValue("Tên thiết bị phải từ 5 đến 20 ký tự")
            return
        }
        registerDevice(DeviceInfoStore(
                qrCode = qr ?: throw Event.qrError,
                name = sName!!
        ))
    }

    private fun registerDevice(deviceInfo: DeviceInfoStore) {
        DeviceSystemRepository.ins.register(deviceInfo, object : Api.ClientListener<Any> {
            override fun onSuccess(response: Any) {
                successLiveData.postValue(true)
            }

            override fun onFailed(code: Int, message: String) {
                failureLiveData.postValue(true)
                BaseData.ins.resetDeviceInfo()
            }

        })
    }


}