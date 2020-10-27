package wee.digital.fpa.ui

import okhttp3.Response
import okhttp3.WebSocket
import wee.digital.fpa.repository.dto.SocketResultResp
import wee.digital.fpa.repository.socket.Socket
import wee.digital.fpa.repository.socket.WebSocketControl
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.library.extension.jsonFormat
import wee.digital.library.extension.parse

class SocketVM : BaseViewModel(), WebSocketControl.WebSocketMonitorListener {

    val webSocket = EventLiveData<WebSocket?>()

    val response = EventLiveData<SocketResultResp>()

    fun connectSocket(token: String) {
        Socket.action.connectWebSocketMonitor(token, this)
    }

    /**
     * [WebSocketControl.WebSocketMonitorListener] implement
     */
    override fun onConnected(socket: WebSocket, response: Response) {
        log.d("on socket connect")
        webSocket.postValue(socket)
    }

    override fun onResult(message: String) {
        val data = message.parse(SocketResultResp::class.java) ?: return
        log.d("on socket response: ${message.jsonFormat()}")
        response.postValue(data)
    }

    override fun onError(socket: WebSocket, t: Throwable) {
        log.d("on socket disconnect")
        webSocket.postValue(null)
    }

}