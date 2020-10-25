package wee.digital.fpa.repository.socket

import android.util.Log

class Socket {

    companion object {
        val action: Socket by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Socket() }
    }

    var webSocketControlMonitorV2: WebSocketControl? = null

    fun connectWebSocketMonitor(token: String, webSocketMonitorListener: WebSocketControl.WebSocketMonitorListener) {
        if (webSocketControlMonitorV2 == null) {
            Log.e("connectWebSocketMonitor", "Open Connect WebSocketMonitor")
            webSocketControlMonitorV2 = WebSocketControl()
            webSocketControlMonitorV2?.addWebSocketListener(webSocketMonitorListener)
            webSocketControlMonitorV2?.openConnect(token)
            return
        }

        closeWebSocketMonitor(object : WebSocketControl.WebSocketMonitorCloseListener {

            override fun onClosed() {
                Log.e("connectWebSocketMonitor", "closeWebSocketMonitor")
                webSocketControlMonitorV2 = null
                connectWebSocketMonitor(token, webSocketMonitorListener)
            }

        })
    }

    fun closeWebSocketMonitor(webSocketMonitorCloseListener: WebSocketControl.WebSocketMonitorCloseListener? = null) {
        webSocketControlMonitorV2?.closeConnect(webSocketMonitorCloseListener)
    }

}