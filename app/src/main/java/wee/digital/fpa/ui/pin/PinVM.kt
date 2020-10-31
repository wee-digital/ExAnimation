package wee.digital.fpa.ui.pin

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import crypto.Crypto
import wee.digital.fpa.data.local.Config
import wee.digital.fpa.repository.dto.FaceArg
import wee.digital.fpa.repository.dto.PinArg
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOReq
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.payment.PaymentArg
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private val retryCount = AtomicInteger()

    val pinArg = object : MutableLiveData<PinArg?>() {
        override fun setValue(value: PinArg?) {
            retryCount.set(Config.PIN_RETRY_COUNT)
            super.setValue(value)
        }
    }

    val retryMessage = EventLiveData<String>()

    val errorMessage = EventLiveData<MessageArg>()

    fun onPinFilled(pinCode: String, paymentArg: PaymentArg?, faceArg: FaceArg?) {
        paymentArg ?: throw Event.paymentArgError
        faceArg ?: throw Event.faceArgError
        val hashCode = Crypto.hash(pinCode)
        val finalCode = Base64.encodeToString(hashCode, Base64.NO_WRAP)

        verifyPinCode(VerifyPINCodeDTOReq(
                uid = faceArg.userID,
                paymentID = paymentArg.paymentId,
                pinCode = finalCode,
                clientIP = paymentArg.clientIp
        ))
    }

    private fun verifyPinCode(req: VerifyPINCodeDTOReq) {

        PaymentRepository.ins.verifyPINCode(dataReq = req, listener = object : Api.ClientListener<PinArg> {
            override fun onSuccess(data: PinArg) {
                onPinVerifySuccess(data)
            }

            override fun onFailed(code: Int, message: String) {
                onPinVerifyFailed(code, message)
            }
        })
    }

    private fun onPinVerifySuccess(data: PinArg) {
        pinArg.postValue(data)
    }

    private fun onPinVerifyFailed(code: Int, message: String? = null) {
        when (code) {
            1 -> {
                onPinVerifyRetry()
            }
            2 -> errorMessage.postValue(MessageArg(
                    title = "Giao dịch bị hủy bỏ",
                    message = "Lỗi thanh toán. Bạn vui lòng chọn thẻ khác".format()
            ))
            else -> {
                errorMessage.postValue(MessageArg.paymentCancelMessage)
            }
        }

    }

    private fun onPinVerifyRetry() {
        when (retryCount.getAndDecrement()) {
            0 -> {
                retryMessage.postValue("Mã PIN không đúng, bạn còn %s lần thử lại".format(retryCount.get()))
            }
            else -> {
                errorMessage.postValue(MessageArg.paymentCancelMessage)
            }

        }
    }

}