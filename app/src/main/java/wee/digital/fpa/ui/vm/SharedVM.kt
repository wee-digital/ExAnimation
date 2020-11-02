package wee.digital.fpa.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.card.CardItem
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.face.FaceArg
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.fpa.ui.pin.PinArg
import wee.digital.fpa.ui.progress.ProgressArg
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SharedVM : ViewModel() {

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

    val timeoutSecond = MutableLiveData<Int>()

    val timeoutEnd = MutableLiveData<Boolean?>()

    fun syncDeviceInfo() {
        deviceInfo.postValue(BaseData.ins.getDeviceInfoPref())
    }

    fun clearData() {
        qrCode.value = null
        message.value = null
        confirm.value = null
        progress.value = null
        payment.value = null
        cardError.value = null
        cardList.value = null
        otpForm.value = null
        qrCode.value = null
    }

    private var disposable: Disposable? = null

    fun startTimeout(intervalInSecond: Int = Timeout.DEFAULT): MutableLiveData<Boolean?> {
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
                            timeoutEnd.postValue(true)
                            timeoutEnd.value = null
                        }
                        it < 0 -> {
                            disposable?.dispose()
                        }
                    }
                }, {})
        return timeoutEnd
    }

    fun stopTimeout() {
        disposable?.dispose()
        timeoutSecond.postValue(-1)
        timeoutEnd.value = null
    }

}