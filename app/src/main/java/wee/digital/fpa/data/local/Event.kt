package wee.digital.fpa.data.local

import wee.digital.fpa.app.toast


object Event {

    val paymentArgError = NullPointerException("MainVM.paymentArg is null value")

    val deviceInfoError = NullPointerException("MainVM.deviceInfo is null value")

    fun initConsumer() {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            toast(e.message ?: "unknown error")
            when (e) {
                paymentArgError -> {

                }
                deviceInfoError -> {

                }
            }
        }
    }
}