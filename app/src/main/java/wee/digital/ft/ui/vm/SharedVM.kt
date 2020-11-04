package wee.digital.ft.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wee.digital.ft.repository.base.BaseData
import wee.digital.ft.repository.model.DeviceInfo
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.base.EventLiveData
import wee.digital.ft.ui.card.CardItem
import wee.digital.ft.ui.confirm.ConfirmArg
import wee.digital.ft.ui.face.FaceArg
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.payment.PaymentArg
import wee.digital.ft.ui.pin.PinArg
import wee.digital.ft.ui.progress.ProgressArg
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SharedVM : ViewModel() {

    var isSplashing = false

    val direction by lazy {
        EventLiveData<NavDirections>()
    }

    val deviceInfo = MutableLiveData<DeviceInfo?>()

    var qrCode = MutableLiveData<JsonObject?>()

    val message = MutableLiveData<MessageArg?>()

    val confirm = MutableLiveData<ConfirmArg?>()

    val progress = MutableLiveData<ProgressArg?>()

    val payment = MutableLiveData<PaymentArg?>()

    val face = EventLiveData<FaceArg?>()

    val pin = MutableLiveData<PinArg?>()

    val cardError = MutableLiveData<String?>()

    val cardList = MutableLiveData<List<CardItem>?>()

    val otpForm = MutableLiveData<String?>()

    val timeoutColor = MutableLiveData<Int>()

    val timeoutSecond = MutableLiveData<Int>()

    fun syncDeviceInfo() {
        deviceInfo.postValue(BaseData.ins.getDeviceInfoPref())
    }

    fun onPaymentCancel() {
        cardError.value = null
        cardList.value = null
        otpForm.value = null
        qrCode.value = null
        message.value = null
        confirm.value = null
        pin.value = null
        face.value = null
        progress.value = null
        qrCode.value = null
        payment.value = null
        direction.postValue(Main.adv)
    }

    private var disposable: Disposable? = null

    fun startTimeout(messageArg: MessageArg) {
        hideProgress()
        message.value = messageArg
        startTimeout(Timeout.ALERT_DIALOG) {
            message.value = null
        }
    }

    fun startTimeout(intervalInSecond: Int, messageArg: MessageArg = MessageArg.timedOutError) {
        hideProgress()
        message.value = messageArg
        startTimeout(intervalInSecond) {
            message.value = null
        }
    }

    fun startTimeout(intervalInSecond: Int, confirmArg: ConfirmArg) {
        hideProgress()
        confirm.value = confirmArg
        startTimeout(intervalInSecond) {
            confirm.value = null
        }
    }

    fun showProgress(progressArg: ProgressArg) {
        stopTimeout()
        progress.postValue(progressArg)
    }

    fun hideProgress() {
        progress.postValue(null)
    }

    private fun startTimeout(intervalInSecond: Int, block: () -> Unit) {
        val waitingCounter = AtomicInteger(intervalInSecond + 1)
        disposable?.dispose()
        disposable = Observable
                .interval(1, 1, TimeUnit.SECONDS)
                .map { waitingCounter.decrementAndGet() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    timeoutSecond.value = it
                    when {
                        it == 0 -> {
                            block()
                        }
                        it < 0 -> {
                            disposable?.dispose()
                        }
                    }
                }, {})
    }

    fun stopTimeout() {
        disposable?.dispose()
        timeoutSecond.postValue(-1)
    }


}