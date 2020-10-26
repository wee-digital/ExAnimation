package wee.digital.fpa.ui.connect

import com.google.gson.JsonObject
import wee.digital.fpa.ui.message.MessageArg

data class ConnectArg(
        val qr: JsonObject? = null,
        var message: MessageArg? = null
)