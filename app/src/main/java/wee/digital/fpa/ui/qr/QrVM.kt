package wee.digital.fpa.ui.qr

import com.google.gson.JsonObject
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.library.extension.jsonFormat

class QrVM : BaseViewModel() {

    private var isQRChecked = false

    val messageLiveData = EventLiveData<String>()

    val qrLiveData = EventLiveData<JsonObject>()

    fun checkQRCode(text: String?) {
        if (isQRChecked) return
        isQRChecked = true

        if (text.isNullOrEmpty()) {
            log.d("QR is empty")
            isQRChecked = false
            messageLiveData.value = null
            return
        }
        FrameUtil.decryptQRCode(text)?.also {
            log.d(text.jsonFormat())
            log.d("QR captured")
            qrLiveData.postValue(it)
            return
        }
        log.d("QR is wrong")
        messageLiveData.value = "Mã không đúng. Bạn vui\nlòng thử lại lần nữa"
        isQRChecked = false
    }

}