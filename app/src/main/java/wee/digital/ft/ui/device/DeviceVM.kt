package wee.digital.ft.ui.device

import com.google.gson.JsonObject
import wee.digital.ft.repository.base.BaseData
import wee.digital.ft.repository.deviceSystem.DeviceSystemRepository
import wee.digital.ft.repository.model.DeviceInfoStore
import wee.digital.ft.repository.network.Api
import wee.digital.ft.shared.Event
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData

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