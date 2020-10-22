package wee.digital.log

import android.util.Log
import com.google.gson.JsonObject
import org.json.JSONObject
import kotlin.reflect.KClass

open class Logger {

    private val tag: String

    private val enable: Boolean

    constructor(string: String, enable: Boolean = BuildConfig.DEBUG) {
        this.tag = if (string.length > 23) string.substring(0, 22) else string
        this.enable = enable
    }

    constructor(cls: KClass<*>, enable: Boolean = BuildConfig.DEBUG) : this(cls.java.simpleName, enable)

    constructor(any: Any, enable: Boolean = BuildConfig.DEBUG) : this(any.javaClass.simpleName, enable)

    fun d(s: JsonObject) {
        val json = s.toString()
        d(JSONObject(json).toString(2))
    }

    fun d(any: Any?) {
        if (enable) Log.d(tag, any.toString())
    }

    fun d(throwable: Throwable?) {
        d(throwable?.message)
    }

    fun i(any: Any?) {
        if (enable) Log.i(tag, any.toString())
    }

    fun i(throwable: Throwable?) {
        i(throwable?.message)
    }

    fun e(any: Any?) {
        if (enable) Log.e(tag, any.toString())
    }

    fun e(throwable: Throwable?) {
        e(throwable?.message)
    }

    fun w(any: Any?) {
        if (enable) Log.w(tag, any.toString())
    }

    fun w(throwable: Throwable?) {
        w(throwable?.message)
    }

    fun wtf(any: Any?) {
        if (enable) Log.wtf(tag, any.toString())
    }

    fun wtf(throwable: Throwable?) {
        wtf(throwable?.message)
    }

    companion object {

        fun breakpoint() {}

        fun error() {
            arrayOf(true)[-1]
        }

        fun crash() {
            throw RuntimeException("crash")
        }

    }

}