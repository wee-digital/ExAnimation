package wee.digital.fpa.ui.pin

import wee.digital.fpa.repository.dto.VerifyPINCodeDTOReq
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOResp
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private val failureCount = AtomicInteger(5)

    val errorMessage = EventLiveData<String?>()

    fun onPinFilled(s: String) {
        verifyPinCode(VerifyPINCodeDTOReq(
                uid = arrayListOf(),
                paymentID = "",
                pinCode = s,
                clientID = "s"
        ))
    }

    private fun verifyPinCode(req: VerifyPINCodeDTOReq) {
        PaymentRepository.ins.verifyPINCode(dataReq = req, listener = object : Api.ClientListener<VerifyPINCodeDTOResp> {
            override fun onSuccess(data: VerifyPINCodeDTOResp) {
                onPinVerifySuccess()
            }

            override fun onFailed(code: Int, message: String) {
                onPinVerifyFailed(code, message)
            }
        })
    }


    private fun onPinVerifySuccess() {

    }

    private fun onPinVerifyFailed(code: Int, message: String? = null) {
        failureCount.decrementAndGet()
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

}