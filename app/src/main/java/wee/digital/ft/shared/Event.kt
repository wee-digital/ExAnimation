package wee.digital.ft.shared

import androidx.lifecycle.MutableLiveData


object Event {

    val liveData = MutableLiveData<Throwable>()

    val paymentArgError = NullPointerException("SharedVM.paymentArg is null")

    val deviceInfoError = NullPointerException("SharedVM.deviceInfo is null")

    val faceArgError = NullPointerException("SharedVM.faceArg is null value")

    val bankDataError = NullPointerException("Bank data error")

    val pinDataError = NullPointerException("SharedVM.deviceInfo is null")

    val otpFormError = NullPointerException("SharedVM.deviceInfo is null")

    val qrError = NullPointerException("SharedVM.deviceInfo is null")

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