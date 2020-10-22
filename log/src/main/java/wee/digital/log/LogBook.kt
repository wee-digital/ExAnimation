package wee.digital.log

import android.app.Application
import androidx.lifecycle.MutableLiveData
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import wee.digital.log.data.CrashItem
import wee.digital.log.data.LogDb
import wee.digital.log.data.LogItem

object LogBook {

    private var mApp: Application? = null

    var app: Application
        set(value) {
            mApp = value
            crashLog = true
        }
        get() {
            if (null == mApp) throw NullPointerException("module not be set")
            return mApp!!
        }

    val updateLiveData = MutableLiveData<Long>()

    val instance: MutableList<LogItem> = mutableListOf()

    val loggingInterceptor: Interceptor
        get() = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request = chain.request()
                val logItem = saveRequest(request)
                try {
                    val response = chain.proceed(request)
                    saveResponse(logItem, response)
                    return response
                } catch (e: Throwable) {
                    saveResponse(logItem, e)
                    throw e
                } finally {
                    notifyLogChanged()
                }
            }

        }

    var crashLog: Boolean = false
        set(value) {
            if (value) Thread.setDefaultUncaughtExceptionHandler { _, e ->
                Thread {
                    var sTrace = ""
                    var traceArr: Array<StackTraceElement> = e.stackTrace
                    for (i in traceArr.indices) {
                        sTrace += "${traceArr[i]}\n"
                    }
                    // If the exception was thrown in a background thread inside
                    // AsyncTask, then the actual exception can be found with getCause
                    // If the exception was thrown in a background thread inside
                    // AsyncTask, then the actual exception can be found with getCause
                    var sCause = ""
                    e.cause?.also {
                        sCause += it.toString()
                        val causeArr = it.stackTrace
                        for (i in causeArr.indices) {
                            sCause += "${causeArr[i]}\n"
                        }
                    }
                    LogDb.instance.crashDao.insert(
                            CrashItem(
                                    title = e.toString(),
                                    trace = sTrace,
                                    cause = sCause
                            )
                    )

                }.start()
            }
        }


    /**
     * Save request
     */
    private fun saveRequest(url: String, method: String, requestBody: String): LogItem {
        if (instance.size > 20) instance.removeAt(0)

        val logItem = LogItem().also {
            it.url = url
            it.method = method
            it.requestBody = requestBody.jsonFormat()
            it.sentRequestTime = System.currentTimeMillis()
        }
        instance.add(logItem)
        notifyLogChanged()
        return logItem
    }

    fun saveRequest(request: Request): LogItem {
        request.newBuilder().build().apply {
            val buffer = Buffer()
            body()?.writeTo(buffer)
            val requestBody = buffer.readUtf8()
            return saveRequest(url().toString(), method(), requestBody)
        }
    }


    /**
     * Save response
     */
    private fun saveResponse(item: LogItem) {
        for (i in instance.lastIndex downTo 0) {
            if (instance[i] == item) {
                instance[i] = item
                notifyLogChanged()
                break
            }
        }
    }

    private fun saveResponse(item: LogItem, code: Int, message: String, responseBody: String?) {
        saveResponse(item.also {
            it.code = code
            it.message = message
            it.responseBody = responseBody.jsonFormat()
            it.receivedResponseTime = System.currentTimeMillis()
        })
    }

    fun saveResponse(item: LogItem, throwable: Throwable?) {
        saveResponse(item.also {
            it.throwable = throwable
            it.receivedResponseTime = System.currentTimeMillis()
        })
    }

    fun saveResponse(item: LogItem, response: Response) {
        response.newBuilder().build().apply {
            val source = body()?.source()
            source?.request(Long.MAX_VALUE)
            val responseBody = source?.buffer()?.clone()?.readUtf8()
            saveResponse(item, code(), message(), responseBody)
        }
    }


    /**
     * Utils
     */
    fun notifyLogChanged() {
        updateLiveData.postValue(System.currentTimeMillis())
    }


    private fun String?.jsonFormat(): String? {
        this ?: return null
        return try {
            val obj = JSONObject(this)
            obj.keys().forEach {
                if (obj.getString(it).length > 256) {
                    obj.put(it, obj.getString(it).substring(0, 256) + "...")
                }
            }
            obj.toString(2)
        } catch (ignore: Exception) {
            null
        }
    }
}