package wee.digital.fpa.ui.pin

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import crypto.Crypto
import wee.digital.fpa.data.local.Config
import wee.digital.fpa.repository.dto.PaymentDTOReq
import wee.digital.fpa.repository.dto.PaymentResponse
import wee.digital.fpa.repository.dto.PinRequest
import wee.digital.fpa.repository.dto.PinResponse
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.face.FaceArg
import wee.digital.fpa.ui.payment.PaymentArg
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    val arg = MutableLiveData<PinArg?>()

    var hasCardError = false

    var hasPayRequest: Boolean = false

    private val restRetriesAtomic = AtomicInteger(Config.PIN_RETRY_COUNT)

    val restRetriesPin = EventLiveData<Int>()

    val otpForm = EventLiveData<String>()

    override fun onStart() {
        hasPayRequest = false
        hasCardError = false
        restRetriesAtomic.set(Config.PIN_RETRY_COUNT)
    }

    /**
     * Pin verify
     */
    fun onPinVerify(pinCode: String, paymentArg: PaymentArg?, faceArg: FaceArg?) {
        paymentArg ?: throw Event.paymentArgError
        faceArg ?: throw Event.faceArgError
        val hashCode = Crypto.hash(pinCode)
        val finalCode = Base64.encodeToString(hashCode, Base64.NO_WRAP)
        val body = PinRequest(
                uid = faceArg.userIdList,
                paymentID = paymentArg.paymentId,
                pinCode = finalCode,
                clientIP = paymentArg.clientIp
        )
        onPinVerify(body)
    }

    private fun onPinVerify(req: PinRequest) {
        PaymentRepository.ins.verifyPINCode(dataReq = req, listener = object : Api.ClientListener<PinResponse> {
            override fun onSuccess(response: PinResponse) {
                onPinVerifySuccess(response)
            }

            override fun onFailed(code: Int, message: String) {
                onPinVerifyFailed(code)
            }
        })
    }

    private fun onPinVerifySuccess(response: PinResponse) {
        arg.value = PinArg(response)
        when {
            response.hasDefaultAccount -> {
                eventLiveData.postValue(PinEvent.PAY_REQUEST)
            }
            else -> {
                eventLiveData.postValue(PinEvent.CARD_REQUIRED)
            }
        }
    }

    private fun onPinVerifyFailed(code: Int) {
        when {
            code == 1 && restRetriesAtomic.getAndDecrement() > 0 -> {
                restRetriesPin.postValue(restRetriesAtomic.get())
            }
            else -> {
                eventLiveData.postValue(PinEvent.PIN_VERIFY_FAILED)
            }
        }
    }

    /**
     * Pay request
     */
    fun onPayRequest(paymentArg: PaymentArg?) {
        paymentArg ?: throw Event.paymentArgError
        val body = PaymentDTOReq(
                paymentID = paymentArg.paymentId,
                clientIP = paymentArg.clientIp,
                accountID = null
        )
        PaymentRepository.ins.payment(body, object : Api.ClientListener<PaymentResponse> {
            override fun onSuccess(response: PaymentResponse) {
                onPayRequestSuccess(response)
            }

            override fun onFailed(response: PaymentResponse) {
                onPayRequestFailed()
            }
        })
    }

    private fun onPayRequestSuccess(response: PaymentResponse) {
        when {
            response.code == 0 -> {
                eventLiveData.postValue(PinEvent.PAY_REQUEST_SUCCESS)
            }
            response.haveOTP && !response.formOtp.isNullOrEmpty() -> {
                otpForm.postValue(response.formOtp)
            }
            else -> {
                hasCardError = true
                eventLiveData.postValue(PinEvent.CARD_ERROR)
            }
        }
    }

    private fun onPayRequestFailed() {
        eventLiveData.postValue(PinEvent.PAY_REQUEST_FAILED)
    }


}