package wee.digital.fpa.repository.socket

import android.util.Log
import okhttp3.*
import okio.ByteString
import wee.digital.fpa.repository.network.LogGrafana
import wee.digital.fpa.repository.utils.SystemUrl
import java.util.concurrent.TimeUnit

class WebSocketControl : WebSocketListener() {

    companion object {
        const val TAG = "WebSocketMonitorV2"
        const val NORMAL_CLOSURE_STATUS = 1000
    }

    private var mClient = OkHttpClient()
    private var mWS: WebSocket? = null
    private var mRequest: Request? = null
    private var mURLConnecting = ""
    private var mURLConnected = ""

    //---
    private var isOpen = false
    private var mTimeIn = 0L
    private var mToken: String? = null

    //---
    private var mWebSocketMonitorListener: WebSocketMonitorListener? = null
    private var mWebSocketMonitorCloseListener: WebSocketMonitorCloseListener? = null
    //---

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d(TAG, "onOpen - $response")
        LogGrafana.instance.postWebSocket("WebSocket onConnected ${response.message}")
        mURLConnected = mURLConnecting
        mWebSocketMonitorListener?.onConnected(webSocket,response)
        isOpen = true
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d(TAG, "onFailure - $t - $response")
        LogGrafana.instance.postWebSocket("onFailure - $t - $response")
        isOpen = false
        mWebSocketMonitorListener?.onError(webSocket,t)
        mWebSocketMonitorCloseListener?.onClosed()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        isOpen = false
        Log.d(TAG, "onClosing - $code - $reason")
        LogGrafana.instance.postWebSocket("onClosing - $code - $reason")
        if (code == 3012) {
            mWebSocketMonitorListener?.onClosing(reason)
            mWebSocketMonitorCloseListener?.onClosed()
        }
        if (code == 1000) {
            mWebSocketMonitorListener?.unKnownError(reason)
            mWebSocketMonitorCloseListener?.onClosed()
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        mWebSocketMonitorListener?.onResult(text)
        LogGrafana.instance.postWebSocket("onResult : $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.d(TAG, "onMessage bytes - $bytes")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d(TAG, "onClosed - [${System.currentTimeMillis() - mTimeIn}] - $code - $reason")
        LogGrafana.instance.postWebSocket("onClosed webSocket - code : $code - reason : $reason")
        isOpen = false
        if (code != 3012 && code != 1000) {
            mWebSocketMonitorListener?.onDisconnected("onClosed - [${System.currentTimeMillis() - mTimeIn}] - $code - $reason")
            mWebSocketMonitorCloseListener?.onClosed()
        }
    }

    fun sendData(data: String) {
        if (isOpen) {
            mWS!!.send(data)
            Log.d("sendData", "Sent - $data")
        } else {
            Log.d("sendData", "WebSocket not Open")
        }
    }

    fun closeConnect(webSocketMonitorCloseListener: WebSocketMonitorCloseListener?) {
        mWebSocketMonitorCloseListener = webSocketMonitorCloseListener
        if (isOpen) {
            mTimeIn = System.currentTimeMillis()
            mWS!!.close(NORMAL_CLOSURE_STATUS, "Usb: $mToken - Say Goodbye !")
        } else {
            mWebSocketMonitorCloseListener?.onClosed()
        }
    }

    fun openConnect(token: String) {
        if (isOpen) return
        val url = "${SystemUrl.SOCKET_URL}$token"
        mToken = token
        mTimeIn = System.currentTimeMillis()
        Log.d("Open Connect", "Connecting")
        //val newUrl = url.replace("http", "ws")
        mURLConnecting = url
        mClient = OkHttpClient
                .Builder()
                .pingInterval(1000, TimeUnit.MILLISECONDS)
                .build()
        Log.d(TAG, "Connecting to $mURLConnecting")
        mRequest = Request.Builder()
                .url(mURLConnecting)
                .build()
        mWS = mClient.newWebSocket(mRequest!!, this)
        mClient.dispatcher.executorService.shutdown()
    }

    fun addWebSocketListener(webSocketMonitorListener: WebSocketMonitorListener) {
        mWebSocketMonitorListener = webSocketMonitorListener
    }

    interface WebSocketMonitorListener {
        fun onConnected(webSocket: WebSocket, response: Response) {}
        fun onDisconnected(message: String) {}
        fun onClosing(message: String) {}
        fun unKnownError(message: String) {}
        fun onResult(message: String)
        fun onError(webSocket: WebSocket, t: Throwable)
    }

    interface WebSocketMonitorCloseListener {
        fun onClosed()
    }


}