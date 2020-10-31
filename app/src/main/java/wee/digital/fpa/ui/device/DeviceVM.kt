package wee.digital.fpa.ui.device

import com.google.gson.JsonObject
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel

class DeviceVM : BaseViewModel() {

    override fun onStart() {
    }

    fun registerDevice(sName: String?, qr: JsonObject?) {
        if (sName?.length ?: 0 < 5) {
            eventLiveData.postValue(DeviceEvent.REGISTER_SUCCESS)
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
                eventLiveData.postValue(DeviceEvent.REGISTER_SUCCESS)
            }

            override fun onFailed(code: Int, message: String) {
                eventLiveData.postValue(DeviceEvent.REGISTER_ERROR)
                BaseData.ins.resetDeviceInfo()
            }

        })
    }


}