package wee.digital.fpa.ui.pin

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Config
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOReq
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOResp
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.payment.PaymentArg
import java.util.concurrent.atomic.AtomicInteger

class PinVM : BaseViewModel() {

    private val retryCount = AtomicInteger(Config.PIN_RETRY_COUNT)

    val retryMessage = EventLiveData<String>()

    val errorMessage = EventLiveData<MessageArg>()

    val pinCodeResponse = MutableLiveData<VerifyPINCodeDTOResp>()

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

        PaymentRepository.ins.verifyPINCode(dataReq = req, listener = object : Api.ClientListener<VerifyPINCodeDTOResp> {
            override fun onSuccess(data: VerifyPINCodeDTOResp) {
                onPinVerifySuccess(data)
            }

            override fun onFailed(code: Int, message: String) {
                onPinVerifyFailed(code, message)
            }
        })
    }

    private fun onPinVerifySuccess(data: VerifyPINCodeDTOResp) {
        pinCodeResponse.postValue(data)
    }

    private fun onPinVerifyFailed(code: Int, message: String? = null) {
        when (code) {
            1 -> {
                onPinVerifyRetry()
            }
            2 -> errorMessage.postValue(MessageArg(
                    headerGuideline = R.id.guidelineConnect,
                    icon = R.mipmap.img_x_mark_flat,
                    title = "Giao dịch bị hủy bỏ",
                    message = "Lỗi thanh toán. Bạn vui lòng chọn thẻ khác".format()
            ))
            else -> {
                errorMessage.postValue(MessageArg.paymentCancelMessage)
            }
        }

    }

    private fun onPinVerifyRetry() {
        when (retryCount.decrementAndGet()) {
            0 -> {
                retryMessage.postValue("Mã PIN không đúng, bạn còn %s lần thử lại".format(retryCount.get()))
            }
            else -> {
                errorMessage.postValue(MessageArg.paymentCancelMessage)
            }

        }
    }

}