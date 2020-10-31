package wee.digital.fpa.ui.otp

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.message.MessageArg
import java.util.concurrent.atomic.AtomicInteger

class OtpVM : BaseViewModel() {

    private val retryCount = AtomicInteger()

    val otpForm = MutableLiveData<String>()

    val retryMessage = EventLiveData<ConfirmArg>()

    val errorMessage = EventLiveData<MessageArg>()

    fun onStart(){
        retryCount.set(1)
    }

    fun onTransactionFailed(data: String) {
        if (retryCount.getAndDecrement() > 0) {
            onPaymentRetry(data)
        } else {
            onPaymentError(data)
        }

    }

    private fun onPaymentRetry(data: String) {
        when (data) {
            "INSUFFICIENT_FUNDS" -> {
                retryMessage.postValue(ConfirmArg(
                        title = "Giao dịch thất bại",
                        message = "Không đủ số dư thanh toán. Bạn vui lòng chọn thẻ khác"
                ))
            }
            "TRANSACTION_BELOW_LIMIT", "TRANSACTION_OUT_OF_LIMIT_BANK" -> {
                retryMessage.postValue(ConfirmArg(
                        title = "Giao dịch thất bại",
                        message = "Quá hạn mức giao dịch. Bạn vui lòng chọn thẻ khác"
                ))
            }
            else -> {
                errorMessage.postValue(MessageArg.paymentCancel)
            }
        }
    }

    private fun onPaymentError(data: String) {
        when (data) {
            "INSUFFICIENT_FUNDS" -> {
                errorMessage.postValue(MessageArg(
                        title = "Giao dịch thất bại",
                        message = "Quá hạn mức giao dịch.",
                        button = null
                ))
            }
            "TRANSACTION_BELOW_LIMIT", "TRANSACTION_OUT_OF_LIMIT_BANK" -> {
                errorMessage.postValue(MessageArg(
                        title = "Giao dịch thất bại",
                        message = "Quá hạn mức giao dịch.",
                        button = null
                ))
            }
            else -> {
                errorMessage.postValue(MessageArg.paymentCancel)
            }
        }
    }
}
