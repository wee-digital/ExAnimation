package wee.digital.fpa.ui.pin

import android.util.Base64
import crypto.Crypto
import wee.digital.fpa.data.local.Config
import wee.digital.fpa.repository.dto.*
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.card.CardItem
import wee.digital.fpa.ui.face.FaceArg
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.payment.PaymentArg
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private val restRetriesAtomic = AtomicInteger(Config.PIN_RETRY_COUNT)

    val pinVerifySuccess = EventLiveData<PinArg>()

    val pinVerifyFailed = EventLiveData<MessageArg>()

    val pinVerifyRetries = EventLiveData<Int>()

    val payRequestSuccess = EventLiveData<Boolean>()

    val payRequestError = EventLiveData<MessageArg>()

    val payRequestCardError = EventLiveData<MessageArg>()

    val cardList = EventLiveData<List<CardItem>>()

    val otpForm = EventLiveData<String>()


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
                pinVerifySuccess.postValue(PinArg(response))
            }

            override fun onFailed(code: Int, message: String) {
                when {
                    code == 1 && restRetriesAtomic.getAndDecrement() > 0 -> {
                        pinVerifyRetries.postValue(restRetriesAtomic.get())
                    }
                    else -> {
                        pinVerifyFailed.postValue(MessageArg(
                                title = "Quá số lần nhập mã PIN",
                                message = "Bạn đã nhập sai mã PIN quá số lần cho phép, giao dịch không thể thực hiện."
                        ))
                    }
                }
            }
        })
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

            override fun onFailed(code: Int, message: String) {
                payRequestError.postValue(MessageArg.fromCode(code))
            }
        })
    }

    private fun onPayRequestSuccess(response: PaymentResponse) {
        when {
            response.code == 0 -> {
                payRequestSuccess.postValue(true)
            }
            response.haveOTP && !response.formOtp.isNullOrEmpty() -> {
                otpForm.postValue(response.formOtp)
            }
            else -> {
                payRequestError.postValue(MessageArg.fromCode(response.code))
            }
        }
    }

    fun fetchCardList(userId: String?) {
        val body = GetBankAccListDTOReq(
                userID = userId ?: throw Event.pinDataError
        )
        PaymentRepository.ins.getBankAccList(body, object : Api.ClientListener<CardListResponse> {
            override fun onSuccess(response: CardListResponse) {
                cardList.postValue(CardItem.getList(response))
            }

            override fun onFailed(code: Int, message: String) {
                cardList.postValue(null)
            }

        })
    }

}