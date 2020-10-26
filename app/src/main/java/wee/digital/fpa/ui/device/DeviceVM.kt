package wee.digital.fpa.ui.device

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.R
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.library.extension.bold
import wee.digital.library.extension.color
import wee.digital.library.extension.string

class DeviceVM : BaseViewModel() {

    val arg = MutableLiveData<DeviceArg?>()

    val nameError = EventLiveData<String?>()

    var registerError = EventLiveData<String?>()

    private var onRegister = false

    fun validateOnRegisterDevice(sName: String?) {
        if (onRegister) return
        onRegister = true
        if (sName?.length ?: 0 < 5) {
            nameError.value = "Tên thiết bị phải từ 5 đến 20 ký tự"
            onRegister = false
            return
        }
        val info = DeviceInfoStore(
                qrCode = arg.value?.qrObj.toString(),
                name = sName!!
        )
        registerDevice(info)
    }

    private fun registerDevice(deviceInfo: DeviceInfoStore) {
        DeviceSystemRepository.ins.register(deviceInfo, object : Api.ClientListener<Any> {
            override fun onSuccess(data: Any) {
                registerError.postValue(null)
            }

            override fun onFailed(code: Int, message: String) {
                onRegister = false
                val s = when (code) {
                    in 1..6, 500 -> {
                        string(R.string.register_failed)
                                .format("Hotline: 1900 2323".bold().color("#212121"))
                    }
                    else -> {
                        "Có lỗi phát sinh, bạn vui lòng\nthử lại lần nữa"
                    }
                }
                registerError.postValue(s)
                BaseData.ins.resetDeviceInfo()
            }

        })
    }


}