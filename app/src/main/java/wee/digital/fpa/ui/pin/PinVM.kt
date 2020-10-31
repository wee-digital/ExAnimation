package wee.digital.fpa.ui.pin

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import crypto.Crypto
import wee.digital.fpa.data.local.Config
import wee.digital.fpa.repository.dto.*
import wee.digital.fpa.repository.dto.PinArg
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.library.extension.post
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private val retryCount = AtomicInteger()

    val pinArg = MutableLiveData<PinArg?>()

    val retryMessage = EventLiveData<String>()

    val errorMessage = EventLiveData<MessageArg>()

    val paymentResult = EventLiveData<Boolean>()

    fun onStart(){
        retryCount.set(Config.PIN_RETRY_COUNT)
    }

    fun onPinFilled(pinCode: String, paymentArg: PaymentArg?, faceArg: FaceArg?) {
        paymentArg ?: throw Event.paymentArgError
        faceArg ?: throw Event.faceArgError
        val hashCode = Crypto.hash(pinCode)
        val finalCode = Base64.encodeToString(hashCode, Base64.NO_WRAP)
        val body = VerifyPINCodeDTOReq(
                uid = faceArg.userID,
                paymentID = paymentArg.paymentId,
                pinCode = finalCode,
                clientIP = paymentArg.clientIp
        )
        verifyPinCode(body, paymentArg, faceArg)
    }

    private fun verifyPinCode(req: VerifyPINCodeDTOReq, paymentArg: PaymentArg, faceArg: FaceArg) {

        PaymentRepository.ins.verifyPINCode(dataReq = req, listener = object : Api.ClientListener<PinArg> {
            override fun onSuccess(data: PinArg) {
                onPinVerifySuccess(data, paymentArg, faceArg)
            }

            override fun onFailed(code: Int, message: String) {
                onPinVerifyFailed(code, message)
            }
        })
    }

    private fun onPinVerifySuccess(data: PinArg, paymentArg: PaymentArg, faceArg: FaceArg) {
        pinArg.postValue(data)
        val body = PaymentDTOReq(

        )
        PaymentRepository.ins.payment(body, object : Api.ClientListener<PaymentDTOResp> {
            override fun onSuccess(data: PaymentDTOResp) {
                when {
                    data.code == 0 -> {
                        // show progress face paid
                    }
                    data.haveOTP && !data.formOtp.isNullOrEmpty() -> {
                        //navigate napas
                    }
                    else -> {
                        // failed
                    }
                }
                paymentResult.postValue(true)
            }

        })
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