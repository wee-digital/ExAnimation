package wee.digital.library.extension

import java.lang.reflect.InvocationTargetException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: Kotlin
 * @Created: Huy QV 2018/08/07
 * @Description: ...
 * None Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
const val SECOND: Long = 1000

const val MIN: Long = 60 * SECOND

const val HOUR: Long = 60 * MIN

val nowInMillis: Long get() = System.currentTimeMillis()

val nowInSecond: Long get() = System.currentTimeMillis() / SECOND

fun nowFormat(format: String): String {
    return timeFormat(nowInMillis, format)
}

fun nowFormat(sdf: SimpleDateFormat): String {
    return timeFormat(nowInMillis, sdf)
}

// if give up time in second convert to time in millis
private fun Long.correctTime(): Long {
    return if (this < 1000000000000L) this * 1000 else this
}

fun timeFormat(long: Long, formatter: SimpleDateFormat): String {
    return try {
        formatter.format(Date(long.correctTime()))
    } catch (e: ParseException) {
        "..."
    } catch (e: InvocationTargetException) {
        "..."
    }
}

fun timeFormat(long: Long, formatter: String): String {
    return try {
        SimpleDateFormat(formatter).format(Date(long.correctTime()))
    } catch (e: ParseException) {
        "..."
    } catch (e: InvocationTargetException) {
        "..."
    }
}





