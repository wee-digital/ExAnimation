package wee.digital.fpa.ui.qr

import com.google.gson.JsonObject
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData

class QrVM : BaseViewModel() {

    private var isQRChecked = false

    val progress = EventLiveData<Boolean>()

    val message = EventLiveData<String?>()

    var qrCode = EventLiveData<JsonObject>()

    fun checkQRCode(text: String?) {
        if (isQRChecked) return
        isQRChecked = true
        if (text.isNullOrEmpty()) {
            message.value = null
            return
        }
        progress.postValue(true)
        FrameUtil.decryptQRCode(text)?.also {
            progress.postValue(false)
            qrCode.postValue(it)
            return
        }
        progress.postValue(false)
        message.value = "Mã không đúng. Bạn vui lòng thử lại lần nữa"
        isQRChecked = false
    }

}