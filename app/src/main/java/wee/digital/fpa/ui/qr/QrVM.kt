package wee.digital.fpa.ui.qr

import wee.digital.fpa.MainDirections
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData

class QrVM : BaseViewModel() {

    private var isQRChecked = false

    val message = EventLiveData<String?>()

    fun checkQRCode(text: String) {
        if (isQRChecked) return
        isQRChecked = true
        if (text.isNotEmpty() && FrameUtil.decryptQRCode(text) != null) {
            Shared.deviceInfo.value?.qrCode = text
            Main.direction.value = MainDirections.actionGlobalDeviceFragment()
            return
        }
        message.value = if (text.isEmpty()) null else {
            "Mã không đúng. Bạn vui lòng thử lại lần nữa"
        }
        isQRChecked = false
    }


}