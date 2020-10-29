package wee.digital.fpa.ui

import wee.digital.fpa.app.toast


object Event {

    val paymentArgError = NullPointerException("PaymentVM.paymentArg is null value")

    val deviceInfoError = NullPointerException("MainVM.deviceInfo is null value")

    val faceArgError = NullPointerException("FaceVM.faceArg is null value")

    val bankDataError = NullPointerException("Bank data error")

    val pinDataError = NullPointerException("Pin data error")

    fun initConsumer() {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            toast(e.message ?: "unknown error")
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