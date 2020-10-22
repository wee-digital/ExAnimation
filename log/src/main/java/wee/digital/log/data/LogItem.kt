package wee.digital.log.data

import java.text.SimpleDateFormat
import java.util.*

class LogItem {

    var url: String = ""

    var method: String = ""

    var sentRequestTime: Long = 0

    var receivedResponseTime: Long = 0

    var requestBody: String? = null

    var code: Int = 0

    var message: String? = null

    var responseBody: String? = null

    var throwable: Throwable? = null

    val ago: String
        get() {
            val ago = System.currentTimeMillis() - sentRequestTime
            if (ago < 60000) {
                return "${(ago / 1000)} seconds ago"
            }
            return SimpleDateFormat("HH:mm:ss").format(Date(sentRequestTime))
        }

    val interval: String
        get() = if (responseBody == null && throwable == null) "~ms"
        else "${(receivedResponseTime - sentRequestTime)}ms"

    override fun equals(other: Any?): Boolean {
        (other as? LogItem)?.also {
            return it.url == this.url && it.sentRequestTime == this.sentRequestTime
        }
        return false
    }
}