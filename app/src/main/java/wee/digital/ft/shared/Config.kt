package wee.digital.ft.shared


object Config {


    const val FACE_RETRY_COUNT = 1

    const val PIN_RETRY_COUNT = 3

    var VIEW_ENABLE = true

    var VM_ENABLE = true

    var TESTING = false

    val DEVICE_NAME_FILTER = charArrayOf(
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M',
            'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' '
    )


}
