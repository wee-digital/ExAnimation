package wee.digital.fpa.ui.qr

import com.google.gson.JsonObject
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData

class QrVM : BaseViewModel() {

    private var isQRChecked = false

    val message = EventLiveData<String?>()

    var qrCode = EventLiveData<JsonObject>()

    fun checkQRCode(text: String?) {
        if (isQRChecked) return
        isQRChecked = true

        if (text.isNullOrEmpty()) {
            isQRChecked = false
            message.value = null
            return
        }
        FrameUtil.decryptQRCode(text)?.also {
            log.d("QR object detect")
            qrCode.postValue(it)
            return
        }
        log.d("QR is wrong")
        message.value = "Mã không đúng. Bạn vui lòng thử lại lần nữa"
        isQRChecked = false
    }

}