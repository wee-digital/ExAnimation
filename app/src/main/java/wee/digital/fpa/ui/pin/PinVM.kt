package wee.digital.fpa.ui.pin

import androidx.lifecycle.ViewModel
import wee.digital.fpa.app.toast
import wee.digital.fpa.ui.base.EventLiveData
import java.util.concurrent.atomic.AtomicInteger

class PinVM : ViewModel() {

    companion object;

    private var failureCount = AtomicInteger(5)

    val errorLiveData = EventLiveData<String?>()

    private fun onPinVerifySuccess() {

    }

    private fun onPinVerifyFailed(code: Int, message: String? = null) {
        val s = when {
            code == 1 && failureCount.get() > 0 -> {
                "Mã PIN không đúng, bạn còn %s lần thử lại".format(failureCount.get())
            }
            code == 2 -> {
                "Lỗi thanh toán. Bạn vui lòng chọn thẻ khác".format()
            }
            else -> {
                message ?: "Lỗi"
            }
        }
        errorLiveData.postValue(s)
    }

    fun onPinFilled(s: String) {
        failureCount.decrementAndGet()
        toast(s)
    }

}