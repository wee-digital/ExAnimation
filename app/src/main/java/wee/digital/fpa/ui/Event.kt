package wee.digital.fpa.ui

import androidx.lifecycle.MutableLiveData


object Event {

    val liveData
        get() = MutableLiveData<Throwable>()

    val paymentArgError
        get() = NullPointerException("PaymentVM.paymentArg is null value")

    val deviceInfoError
        get() = NullPointerException("MainVM.deviceInfo is null value")

    val faceArgError
        get() = NullPointerException("FaceVM.faceArg is null value")

    val bankDataError
        get() = NullPointerException("Bank data error")

    val pinDataError
        get() = NullPointerException("Pin data error")

    fun initConsumer() {
        return
        Thread.setDefaultUncaughtExceptionHandler { _, e ->

            liveData.postValue(e)
            when (e) {
                paymentArgError -> {

                }
                deviceInfoError -> {

                }
                faceArgError -> {

                }
                bankDataError -> {

                }
            }
        }
    }
}