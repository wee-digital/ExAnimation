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
import wee.digital.library.extension.post
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SharedVM : ViewModel() {

    var isSplashing = false

    val direction by lazy {
        MutableLiveData<NavDirections>()
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


    fun clearData() {
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
        timeoutSecond.value = -1
    }

    fun onPaymentCancel() {
        clearData()
        payment.value = null
        direction.postValue(Main.adv)
    }

    private var disposable: Disposable? = null

    fun startTimeout(messageArg: MessageArg) {
        hideProgress()
        message.postValue(messageArg)
        startTimeout(Timeout.ALERT_DIALOG) {
            onPaymentCancel()
        }
    }

    fun startTimeout(intervalInSecond: Int, messageArg: MessageArg) {
        hideProgress()
        startTimeout(intervalInSecond) {
            message.postValue(messageArg)
            post(200) {
                startTimeout(Timeout.ALERT_DIALOG) {
                    onPaymentCancel()
                }
            }
        }
    }

    fun startTimeout(intervalInSecond: Int, confirmArg: ConfirmArg) {
        hideProgress()
        stopTimeout()
        confirm.postValue(confirmArg)
        startTimeout(intervalInSecond) {
            post(200) {
                onPaymentCancel()
            }
        }
    }

    fun startTimeout(intervalInSecond: Int, block: () -> Unit) {
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

    fun showProgress(progressArg: ProgressArg) {
        stopTimeout()
        progress.postValue(progressArg)
    }

    fun hideProgress() {
        progress.postValue(null)
    }


    fun stopTimeout() {
        disposable?.dispose()
        timeoutSecond.postValue(-1)
    }


}