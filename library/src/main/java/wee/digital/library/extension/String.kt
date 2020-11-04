package wee.digital.library.extension

import android.util.Base64
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import org.json.JSONObject
import wee.digital.library.Library
import java.text.DecimalFormat
import java.text.Normalizer
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

fun String?.normalizer(): String? {
    this ?: return null
    return try {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        pattern.matcher(temp)
                .replaceAll("")
                .toLowerCase()
                .replace(" ", "-")
                .replace("đ", "d", true)

    } catch (e: IllegalStateException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun <T> String?.search(collection: Collection<T>?, block: (T) -> String): Collection<T>? {

    if (collection.isNullOrEmpty()) return null

    if (this.isNullOrEmpty()) return collection

    val searchResults = mutableListOf<T>()
    val s1 = this.normalizer() ?: return null
    for (model in collection) {
        val s2 = block(model).normalizer() ?: continue
        if (s2.contains(s1, true)) searchResults.add(model)
    }
    return searchResults
}

private val decimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat

fun String?.moneyFormat(currency: String? = "VND"): String {
    this ?: return ""
    return try {
        if (currency != null && currency != "VND") {

            if (last().toString() == ".") return this

            val lgt = length
            if (lgt > 1 && substring(lgt - 2, lgt) == ".0") return this
            if (lgt > 2 && substring(lgt - 3, lgt) == ".00") return this

            val docId = indexOf(".")
            if (docId != -1 && substring(docId, length).length > 3) return substring(0, docId + 3)

        }
        var originalString = when (currency) {
            null, "VND" -> this.replace(".", "")
            else -> this
        }
        if (originalString.contains(",")) {
            originalString = originalString.replace(",".toRegex(), "")
        }
        when (currency) {
            null, "VND" -> {
                val value = originalString.toLong()
                decimalFormat.applyPattern("#,###,###,###")
                decimalFormat.format(value)
            }
            else -> {
                val value = originalString.toDouble()
                decimalFormat.applyPattern("#,###,###,###.##")
                decimalFormat.format(value)
            }
        }

    } catch (nfe: Exception) {
        ""
    }
}

fun String?.color(@ColorRes color: Int): String {
    return color(
            "#${
                Integer.toHexString(
                        ContextCompat.getColor(
                                Library.app,
                                color
                        ) and 0x00ffffff
                )
            }"
    )
}

fun String?.color(color: String): String {
    this ?: return ""
    return "<font color=$color>$this</font>"
}

fun String?.bold(): String {
    this ?: return ""
    return "<b>$this</b>"
}

fun String?.notNullOrEmpty(): Boolean {
    return !isNullOrEmpty()
}

fun String?.jsonFormat(): String? {
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

fun Long.cashToText(): String {

    val number = this.toString()

    var text = ""
    var startIndex = number.length - 3
    var endIndex = number.length
    var unit = " đồng"

    while (startIndex >= -2) {

        val sCash = number.substring(if (startIndex > -1) startIndex else 0, endIndex)

        text = " ${cashText(sCash)}$unit$text"
        startIndex -= 3
        endIndex -= 3
        unit = when (unit) {
            " nghìn" -> " triệu"
            " triệu" -> " tỷ"
            " đồng" -> " nghìn"
            else -> " nghìn"
        }
    }

    text = text.replace("  ", " ")
            .trim()
            .replace("tỷ triệu nghìn đồng", "tỷ đồng")
            .replace("triệu nghìn đồng", "triệu đồng")

    return text.substring(0, 1).toUpperCase() + text.substring(1, text.length)
}

val String.encodedFromBase64: String get() = String(Base64.decode(this, Base64.NO_WRAP))

private fun cashText(numText: String): String {

    val n = numText.toLong()
    val n100 = n / 100
    val n10 = n / 10 % 10
    val n1 = n % 10

    if (n100 == 0L && n10 == 0L && n1 == 0L) return ""

    val s100 = if (numText.length < 3) "" else when (n100) {
        0L -> "không trăm"
        1L -> "một trăm"
        2L -> "hai trăm"
        3L -> "ba trăm"
        4L -> "bốn trăm"
        5L -> "năm trăm"
        6L -> "sáu trăm"
        7L -> "bảy trăm"
        8L -> "tám trăm"
        else -> "chín trăm"
    }

    val s10 = if (numText.length < 2) "" else when (n10) {
        0L -> if (n1 == 0L) "" else " lẻ"
        1L -> " mười"
        2L -> " hai mươi"
        3L -> " ba mươi"
        4L -> " bốn mươi"
        5L -> " năm mươi"
        6L -> " sáu mươi"
        7L -> " bảy mươi"
        8L -> " tám mươi"
        else -> " chín mươi"
    }

    val s1 = when (n1) {
        0L -> ""
        1L -> if (n10 < 2) " một" else " mốt"
        2L -> " hai"
        3L -> " ba"
        4L -> " bốn"
        5L -> if (n10 == 0L) " năm" else " lăm"
        6L -> " sáu"
        7L -> " bảy"
        8L -> " tám"
        else -> " chín"
    }
    return "$s100$s10$s1"

}