package wee.digital.ft.ui.qr

import com.google.gson.JsonObject
import wee.digital.ft.camera.FrameUtil
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
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