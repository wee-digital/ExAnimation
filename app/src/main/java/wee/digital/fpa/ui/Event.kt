package wee.digital.fpa.ui

import androidx.lifecycle.MutableLiveData


object Event {

    val liveData = MutableLiveData<Throwable>()


    val paymentArgError = NullPointerException("PaymentVM.paymentArg is null value")

    val deviceInfoError = NullPointerException("MainVM.deviceInfo is null value")

    val faceArgError = NullPointerException("FaceVM.faceArg is null value")

    val bankDataError = NullPointerException("Bank data error")

    val pinDataError = NullPointerException("Pin data error")

    val otpFormError = NullPointerException("OTP form data error")

    val qrError = NullPointerException("QR data error")

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