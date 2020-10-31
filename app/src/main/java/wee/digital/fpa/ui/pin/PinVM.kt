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
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private val retryCount = AtomicInteger()

    val pinArg = MutableLiveData<PinArg?>()

    val paymentSuccess = EventLiveData<PaymentDTOResp?>()

    val pinVerifyError = EventLiveData<Boolean>()

    val pinVerifyRetry = EventLiveData<Int>()

    val cardRequired = EventLiveData<Boolean>()

    val cardError = EventLiveData<Boolean>()

    val otpRequired = EventLiveData<PaymentDTOResp?>()

    fun onStart() {
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
        postPinVerify(body, paymentArg)
    }

    /**
     * @return [onPinVerifySuccess]
     * @return [onPinVerifyFailed]
     */
    private fun postPinVerify(req: VerifyPINCodeDTOReq, paymentArg: PaymentArg) {
        PaymentRepository.ins.verifyPINCode(dataReq = req, listener = object : Api.ClientListener<PinArg> {
            override fun onSuccess(data: PinArg) {
                onPinVerifySuccess(data, paymentArg)
            }

            override fun onFailed(code: Int, message: String) {
                onPinVerifyFailed(code, message)
            }
        })
    }

    /**
     * @return: [postPayRequest]
     * @return: [cardRequired].postValue
     */
    private fun onPinVerifySuccess(data: PinArg, paymentArg: PaymentArg) {
        pinArg.value = data
        when {
            data.hasDefaultAccount -> {
                postPayRequest(paymentArg)
            }
            else -> {
                cardRequired.postValue(true)
            }
        }
    }

    /**
     * @return: [onPinVerifyRetry]
     * @return: [pinVerifyError].postValue
     */
    private fun onPinVerifyFailed(code: Int, message: String? = null) {
        when (code) {
            1 -> {
                onPinVerifyRetry()
            }
            else -> {
                pinVerifyError.postValue(true)
            }
        }
    }

    /**
     * @return: [paymentSuccess].postValue
     * @return: [otpRequired].postValue
     * @return: [cardError].postValue
     */
    private fun postPayRequest(paymentArg: PaymentArg) {
        val body = PaymentDTOReq(
                paymentID = paymentArg.paymentId,
                clientIP = paymentArg.clientIp,
                accountID = null
        )
        PaymentRepository.ins.payment(body, object : Api.ClientListener<PaymentDTOResp> {
            override fun onSuccess(data: PaymentDTOResp) {
                when {
                    data.code == 0 -> {
                        paymentSuccess.postValue(data)
                    }
                    data.haveOTP && !data.formOtp.isNullOrEmpty() -> {
                        otpRequired.postValue(data)
                    }
                    else -> {
                        cardError.postValue(true)
                    }
                }
            }
        })
    }

    /**
     * @return: [pinVerifyRetry].postValue
     * @return: [pinVerifyError].postValue
     */
    private fun onPinVerifyRetry() {
        when (retryCount.getAndDecrement()) {
            0 -> {
                pinVerifyRetry.postValue(retryCount.get())
            }
            else -> {
                pinVerifyError.postValue(true)
            }

        }
    }


}