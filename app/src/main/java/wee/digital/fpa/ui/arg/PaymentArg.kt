package wee.digital.fpa.ui.arg


class PaymentArg(
        val clientIp: String,
        val paymentId: String,
        val amount: String,
        val timeout: Int = 0
)