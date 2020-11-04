package wee.digital.ft.ui.payment

import wee.digital.ft.repository.dto.SocketResponse
import wee.digital.ft.repository.model.ClientResponse


class PaymentArg {

    val clientIp: String
    val paymentId: String
    val amount: String
    val timeout: Int

    constructor(socket: SocketResponse, client: ClientResponse) {
        clientIp = client.ip
        paymentId = socket.paymentId
        amount = socket.amount
        timeout = socket.timeOut
    }

    private constructor() {
        clientIp = "as"
        paymentId = "aa"
        amount = "10000"
        timeout = 10
    }

    companion object {
        val testArg = PaymentArg()
    }

}