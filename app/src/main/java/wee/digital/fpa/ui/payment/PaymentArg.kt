package wee.digital.fpa.ui.payment

import wee.digital.fpa.repository.dto.SocketResponse
import wee.digital.fpa.repository.model.ClientResponse


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

}