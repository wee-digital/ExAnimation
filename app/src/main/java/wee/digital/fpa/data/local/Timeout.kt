package wee.digital.fpa.data.local

object Timeout {

    private const val EXTRA = 100
    const val PAYMENT_CONFIRM = 15 + EXTRA   //15
    const val FACE_VERIFY = 25 + EXTRA      //15
    const val PIN_VERIFY = 10 + EXTRA      //10
    const val DEFAULT = 10
}