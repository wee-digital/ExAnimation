package wee.digital.fpa.ui.pin

import wee.digital.fpa.app.toast
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOReq
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOResp
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private var failureCount = AtomicInteger(5)

    val errorMessage = EventLiveData<String?>()

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
        errorMessage.postValue(s)
    }

    fun onPinFilled(s: String) {
        failureCount.decrementAndGet()
        toast(s)
    }

    fun verifyPinCode(req: VerifyPINCodeDTOReq) {
        PaymentRepository.ins.verifyPINCode(dataReq = req, listener = object : Api.ClientListener<VerifyPINCodeDTOResp> {

            override fun onSuccess(data: VerifyPINCodeDTOResp) {
                toast("verify pin success")
            }

            override fun onFailed(code: Int, message: String) {
                super.onFailed(code, message)
                toast("fail")
            }

        })
    }

}