package wee.digital.fpa.ui.device

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.R
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.library.extension.bold
import wee.digital.library.extension.color
import wee.digital.library.extension.string

class DeviceVM : BaseViewModel() {

    val stationName = MutableLiveData<String>()

    val errorText = EventLiveData<String?>()


    private var onRegister = false

    fun syncStationName() {
        val s = FrameUtil.decryptQRCode(Shared.deviceInfo.value?.qrCode ?: "")
                ?.get("FullName")
                ?.asString ?: ""
        stationName.value = string(R.string.device_hi).format(s.bold())
    }

    fun validateOnRegisterDevice(name: String) {
        if (onRegister) return
        onRegister = true
        if (name.length < 5) {
            errorText.value = "Tên thiết bị phải từ 5 đến 20 ký tự"
            return
        }
        Shared.deviceInfo.value?.also {
            registerDevice(it)
            return
        }
        onRegisterFailed()
    }

    private fun registerDevice(deviceInfo: DeviceInfoStore) {
        DeviceSystemRepository.ins.register(deviceInfo, object : Api.ClientListener<Any> {
            override fun onSuccess(data: Any) {
                onRegisterSuccess()
            }

            override fun onFailed(code: Int, message: String) {
                when (code) {
                    in 1..6, 500 -> ""
                    else -> ""
                }
                BaseData.ins.resetDeviceInfo()
                onRegisterFailed()
            }

        })
    }

    private fun onRegisterSuccess() {
        Main.messageArg.value = MessageArg(
                icon = R.mipmap.img_checked_flat,
                title = "Sample title",
                message = string(R.string.register_success)
                        .format("pos.facepay.vn".bold().color("#378AE1"))
        )
    }

    private fun onRegisterFailed() {
        Main.messageArg.value = MessageArg(
                icon = R.mipmap.img_x_mark_flat,
                title = "Sample title",
                message = string(R.string.register_failed)
                        .format("Hotline: 1900 2323".bold().color("#212121"))
        )
    }

    private fun temp() {
        Main.messageArg.value = MessageArg(
                icon = R.mipmap.img_x_mark_flat,
                title = "Sample title",
                message = "Có lỗi phát sinh, bạn vui lòng\nthử lại lần nữa"
        )
    }
}