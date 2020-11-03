package wee.digital.ft.ui.vm

import okhttp3.Response
import okhttp3.WebSocket
import wee.digital.ft.repository.dto.SocketResponse
import wee.digital.ft.repository.socket.Socket
import wee.digital.ft.repository.socket.WebSocketControl
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
import wee.digital.library.extension.jsonFormat
import wee.digital.library.extension.parse

class SocketVM : BaseViewModel(), WebSocketControl.WebSocketMonitorListener {

    val webSocketLiveData = EventLiveData<WebSocket?>()

    val responseLiveData = EventLiveData<SocketResponse>()

    fun connectSocket(token: String) {
        Socket.action.connectWebSocketMonitor(token, this)
    }

    /**
     * [WebSocketControl.WebSocketMonitorListener] implement
     */
    override fun onConnected(socket: WebSocket, response: Response) {
        log.d("WebSocketMonitorListener.onConnected: ${socket.request().url}")
        webSocketLiveData.postValue(socket)
    }

    override fun onMessage(message: String) {
        val data = message.parse(SocketResponse::class.java) ?: return
        log.d("WebSocketMonitorListener.onMessage: ${message.jsonFormat()}")
        responseLiveData.postValue(data)
    }

    override fun onError(socket: WebSocket, t: Throwable) {
        log.d("WebSocketMonitorListener.onError: $${socket.request().url} ${t.message}")
        webSocketLiveData.postValue(null)
    }

}