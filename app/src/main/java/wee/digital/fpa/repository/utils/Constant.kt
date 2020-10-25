package wee.digital.fpa.repository.utils


object ErrCode {
    /*code server*/
    const val UNKNOWN = -1
    const val INVALID_BODY_DATA = 1
    const val INVALID_SESSION = 2
    const val INVALID_REQUEST_DATA = 3
    const val DecryptBodyDataError = 4
    const val DECRYPT_BODY_DATA = 4
    const val DECRYPT_JSON_DATA = 5
    const val SESSION_ID_NOT_EXIST = 6
    const val DEVICE_REGISTERED = 7
    const val DEVICE_NOT_FOUND = 8
    const val PAYMENT_ID_NOT_FOUND = 9
    const val MAX_FACE_VERIFICATION = 10
    const val NO_FACE = 11
    const val FACE_EXISTED = 12
    const val WRONG_FACE = 13
    const val FACE_INVALID_PARAMETER = 14
    const val FACE_SERVER = 15
    const val USER_NOT_FOUND = 16
    const val ACC_FACEPAY_LOCKED = 17
    const val ACC_LOCKED = 18
    const val TRANS_EXCEED_LIMIT = 19
    const val NOT_LINKED_BANK_ACC = 20
    const val MAX_PIN_CODE_VERIFICATION = 21
    const val MISSING_VERIFIED_FACE = 22
    const val WRONG_PIN_CODE = 23
    const val MAX_FACE_PAY = 24
    const val MISSING_VERIFIED_FACE_OR_PIN_CODE = 25
    const val BILL_PAID = 26
    const val USER_BANK_ACC_NOT_FOUND = 27
    const val BANK_ACC_NOT_FOUND = 28
    const val BANK_UNKNOWN_ERROR = 29
    const val UPDATE_PAYMENT_STATUS = 30
    const val USER_IS_OWNER = 31
    const val INSUFFICIENT_ACC_BALANCE = 32

    /*code convert case server to client*/

    const val SUCCESS = 0

    //register device
    const val REGISTER_DEVICE_FAIL = 101

    //check status device
    const val DEVICE_EXISTS = 103
    const val DEVICE_FAIL = 104
    const val DEVICE_DELETE = 105

    // listener code popBackStack in webViewOtp
    const val GO_BACK_HOME = 106

    // key status animation banner
    const val KEY_STATUS_BANNER = "KEY_STATUS_BANNER"

    //get token webSocket error
    const val GET_TOKEN_SOCKET_FAIL = 107

    //code verifyFace
    const val FACE_NOT_FOUND = 109

    //code payment
    const val TIMEOUT_ERROR = 112
    const val FACE_PAY_ERROR = 113

    //api fail
    const val ENCRYPT_DATA_FAIL = -1005
    const val API_FAIL = -1006
    const val CONVERT_DATA_FAIL = -1007
}

object Key {
    const val PREPARE_KEY = "0iza/3OySf6+T43vMjxMrdabnhtmsQm43uSwU0/zQc4="
    const val RSA_COLLECTDATA = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4t2A1nc1hQQWNj7q90PyRP1N+gX8ucD+fmGKeqi9cNb7uwSCVvpo/jaIWNVCcK0qAa0x6uJ3mmNWbispyXiJvRlxbeuNEzEpNgHZo8+GS4lWbuaSWxg0kWKc73dlM02Mz89IfWjIQuMlToE+SX9z19nelgQInX+k6uR121x+5/Dj6Z1tuFllE2DFtD1QBwriL2LreLrIkTxewYuIDj1WsdcRPOo5zq1aFi/bMIo33hBn8tRf6QDxWyVtAbtTSxlqUHCJtZvrsdePvQ6etrbA6+KtttSIpxYDeWUwqdcgfxXmsuxHJCofzWPLUld/DbBwmmMyFmC9hsPUNeaesl2AtQIDAQAB"
}

object URLType {
    const val DEV = "URL_DEV"
    const val QC = "URL_QC"
    const val BETA = "URL_BETA"
}

object ADVType {
    const val WEE = "ADV_WEE"
    const val TCH = "ADV_TCH"
    const val GS25 = "ADV_GS25"
    const val SEVEN = "ADV_SEVEN"
    const val TOUS = "ADV_TOUS"
    const val FIREBASE = "ADV_FIREBASE"
}

object PaymentStatusCode {
    const val USER_REJECTED = 7
    const val OVER_LIMIT = 9
    const val DEVICE_PROCESSING = 15
    const val CANCEL_PAYMENT = 16
    const val CANCEL_PAYMENT_PAUSE_ACTIVITY = 17
}

object CancelPaymentCode {
    const val SCREEN_PAYING = 2
    const val SCREEN_WAITING = 1
    const val CANCEL_SUCCESS = 0
}

object SocketEvent {
    const val EVENT_PAYMENT = "POSPR"
    const val EVENT_CANCEL = "POSCR"
    const val EVENT_DELETE = "POSD"
}

object SystemUrl {
    const val BASE_URL_CLIENT_ID = "http://weezi.biz:8481/"
    const val BASE_URL_COLLECT = "http://weezi.biz:8580/"

    var BASE_URL = "https://dev.facepay.vn/v1/"
    var BASE_URL_VIDEO = "https://dev.facepay.vn/"
    var SOCKET_URL = "wss://dev.facepay.vn/ws/transaction?token="
    var AVATAR_URL = "${BASE_URL}get/thumbnail?id="
    var LOGO_SHOP = "https://dev.facepay.vn/v1/shop/avatar?id="

    var IMAGE_BANK_NEW = "https://dev.facepay.vn/v1/images/bank_iconic_color/3.0x/"
    var URL_GET_BANKS = "https://dev.facepay.vn/v1/resources/"

    const val BASE_URL_LOG = "http://logs.wee.vn:3100/loki/api/v1/"
}