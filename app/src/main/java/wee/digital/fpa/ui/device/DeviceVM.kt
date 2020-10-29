package wee.digital.fpa.ui.device

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.arg.MessageArg
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.library.extension.bold
import wee.digital.library.extension.color
import wee.digital.library.extension.string

class DeviceVM : BaseViewModel() {

    val nameError = EventLiveData<String?>()

    var registerError = EventLiveData<MessageArg>()

    var registerSuccess = EventLiveData<MessageArg>()

    val progressVisible = EventLiveData<Boolean>()

    fun registerDevice(sName: String?,qr: JsonObject?) {
        if (progressVisible.value == true) return
        if (sName?.length ?: 0 < 5) {
            nameError.value = "Tên thiết bị phải từ 5 đến 20 ký tự"
            return
        }
        val info = DeviceInfoStore(
                qrCode = qr ?: JsonObject(),
                name = sName!!
        )
        registerDevice(info)
    }

    private fun registerDevice(deviceInfo: DeviceInfoStore) {
        progressVisible.postValue(true)
        DeviceSystemRepository.ins.register(deviceInfo, object : Api.ClientListener<Any> {
            override fun onSuccess(data: Any) {
                progressVisible.postValue(false)
                onRegisterSuccess()
            }

            override fun onFailed(code: Int, message: String) {
                progressVisible.postValue(false)
                val s = when (code) {
                    in 1..6, 500 -> {
                        string(R.string.register_failed)
                                .format("Hotline: 1900 2323".bold().color("#212121"))
                    }
                    else -> {
                        "Có lỗi phát sinh, bạn vui lòng\nthử lại lần nữa"
                    }
                }
                onRegisterError(s)
                BaseData.ins.resetDeviceInfo()
            }

        })
    }

    private fun onRegisterSuccess() {
        val message = MessageArg(
                headerGuideline = R.id.guidelineConnect,
                icon = R.mipmap.img_checked_flat,
                title = string(R.string.device_register_success),
                button = string(R.string.device_register_finish),
                message = string(R.string.register_success)
                        .format("pos.facepay.vn".bold().color("#378AE1")),
                onClose = {
                    Main.rootDirection.value = MainDirections.actionGlobalSplashFragment()
                }
        )
        registerSuccess.postValue(message)
    }

    private fun onRegisterError(s: String?) {
        val message = MessageArg(
                headerGuideline = R.id.guidelineConnect,
                icon = R.mipmap.img_x_mark_flat,
                title = string(R.string.device_register_failed),
                button = string(R.string.device_register_fail),
                message = s
        )
        registerError.postValue(message)
    }

}