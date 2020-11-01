package wee.digital.fpa.ui.pin

import wee.digital.fpa.repository.dto.PinResponse

class PinArg {

    var haveOTP: Boolean
    var otpForm: String
    var hasDefaultAccount: Boolean
    var userId: String

    constructor(response: PinResponse) {
        haveOTP = response.haveOTP
        otpForm = response.formOtp
        hasDefaultAccount = response.hasDefaultAccount
        userId = response.userId
    }

}