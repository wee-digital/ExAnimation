package wee.digital.ft.shared

object Timeout {

    private const val EXTRA = 10
    const val PAYMENT_DISMISS = 5
    const val PAYMENT_CONFIRM = 15 + EXTRA
    const val FACE_VERIFY = 25 + EXTRA
    const val PIN_VERIFY = 10 + EXTRA
    const val CARD_SELECT = 10 + EXTRA
}