package wee.digital.fpa.data.local

object Timeout {

    private const val EXTRA = 100
    const val PAYMENT_CONFIRM = 15 + EXTRA
    const val FACE_VERIFY = 15 + EXTRA
    const val FACE_RETRY = 5 + EXTRA
    const val PIN_VERIFY = 10 + EXTRA
}