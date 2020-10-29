package wee.digital.fpa.ui.pin

import wee.digital.fpa.data.local.Config
import wee.digital.fpa.data.local.Event
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOReq
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOResp
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private val retryCount = AtomicInteger(Config.PIN_RETRY_COUNT)

    val errorMessage = EventLiveData<String?>()

    val pinCodeSuccess = EventLiveData<Boolean>()

    fun onPinFilled(pinCode: String, paymentArg: PaymentArg?, deviceInfo: DeviceInfo?) {
        paymentArg ?: throw Event.paymentArgError
        deviceInfo ?: throw Event.deviceInfoError
        verifyPinCode(VerifyPINCodeDTOReq(
                uid = arrayListOf(deviceInfo.uid),
                paymentID = paymentArg.paymentId,
                pinCode = pinCode,
                clientID = paymentArg.clientIp
        ))
    }

    private fun verifyPinCode(req: VerifyPINCodeDTOReq) {
        retryCount.decrementAndGet()
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
        pinCodeSuccess.postValue(true)
    }

    private fun onPinVerifyFailed(code: Int, message: String? = null) {
        val s = when {
            code == 1 && retryCount.get() > 0 -> {
                "Mã PIN không đúng, bạn còn %s lần thử lại".format(retryCount.get())
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