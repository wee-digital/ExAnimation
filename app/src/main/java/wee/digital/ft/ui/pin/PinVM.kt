package wee.digital.ft.ui.pin

import android.util.Base64
import crypto.Crypto
import wee.digital.ft.repository.dto.*
import wee.digital.ft.repository.network.Api
import wee.digital.ft.repository.payment.PaymentRepository
import wee.digital.ft.repository.utils.ErrCode
import wee.digital.ft.shared.Config
import wee.digital.ft.shared.Event
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
import wee.digital.ft.ui.card.CardItem
import wee.digital.ft.ui.face.FaceArg
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.payment.PaymentArg
import wee.digital.library.extension.post
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private val restRetriesAtomic = AtomicInteger(Config.PIN_RETRY_COUNT)

    val pinVerifySuccess = EventLiveData<PinArg>()

    val pinVerifyFailed = EventLiveData<MessageArg>()

    val pinVerifyRetries = EventLiveData<Int>()

    val payRequestSuccess = EventLiveData<PaymentResponse>()

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
        if (Config.TESTING) post(2000) {
            pinVerifySuccess.value = PinArg.testArg
            return@post
        }
        PaymentRepository.ins.verifyPINCode(dataReq = req, listener = object : Api.ClientListener<PinResponse> {
            override fun onSuccess(response: PinResponse) {

                when (response.code) {
                    0 -> {
                        pinVerifySuccess.postValue(PinArg(response))
                    }
                    ErrCode.PIN_LIMIT, ErrCode.PIN_WRONG -> {
                        onVerifyPinFailed()
                    }
                    else -> {
                        payRequestError.postValue(MessageArg.fromCode(response.code))
                    }

                }
            }

            override fun onFailed(code: Int, message: String) {
                when (code) {
                    ErrCode.PIN_LIMIT, ErrCode.PIN_WRONG -> {
                        onVerifyPinFailed()
                    }
                    else -> {
                        payRequestError.postValue(MessageArg.fromCode(code))
                    }
                }
            }
        })
    }

    private fun onVerifyPinFailed() {
        when {
            restRetriesAtomic.getAndDecrement() > 1 -> {
                pinVerifyRetries.postValue(restRetriesAtomic.get())
            }
            else -> {
                pinVerifyFailed.postValue(MessageArg.wrongPinManyTimes)
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
                payRequestSuccess.postValue(response)
            }

            override fun onFailed(code: Int, message: String) {
                payRequestError.postValue(MessageArg.fromCode(code))
            }
        })
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