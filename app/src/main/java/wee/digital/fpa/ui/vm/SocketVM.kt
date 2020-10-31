package wee.digital.fpa.ui.vm

import okhttp3.Response
import okhttp3.WebSocket
import wee.digital.fpa.repository.dto.SocketResponse
import wee.digital.fpa.repository.socket.Socket
import wee.digital.fpa.repository.socket.WebSocketControl
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.library.extension.jsonFormat
import wee.digital.library.extension.parse

class SocketVM : BaseViewModel(), WebSocketControl.WebSocketMonitorListener {

    val webSocket = EventLiveData<WebSocket?>()

    val response = EventLiveData<SocketResponse>()

    override fun onStart() {
    }

    fun connectSocket(token: String) {
        Socket.action.connectWebSocketMonitor(token, this)
    }

    /**
     * [WebSocketControl.WebSocketMonitorListener] implement
     */
    override fun onConnected(socket: WebSocket, response: Response) {
        log.d("WebSocketMonitorListener.onConnected: ${socket.request().url}")
        webSocket.postValue(socket)
    }

    override fun onMessage(message: String) {
        val data = message.parse(SocketResponse::class.java) ?: return
        log.d("WebSocketMonitorListener.onMessage: ${message.jsonFormat()}")
        response.postValue(data)
    }

    override fun onError(socket: WebSocket, t: Throwable) {
        log.d("WebSocketMonitorListener.onError: $${socket.request().url} ${t.message}")
        webSocket.postValue(null)
    }

}