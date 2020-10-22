package wee.digital.library.extension

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.io.StringReader
import java.math.BigDecimal

private val convertFactory: Gson = Gson()

fun <T> T.toJsonObject(): JsonObject? {
    return try {
        val element = convertFactory.toJsonTree(this, object : TypeToken<T>() {}.type)
        return element.asJsonObject
    } catch (ignore: Exception) {
        null
    }
}

fun <T> Collection<T?>?.toJsonArray(): JsonArray? {
    if (this.isNullOrEmpty()) return null
    val jsonArray = JsonArray()
    this.forEach {
        when (it) {
            is JsonObject -> {
                jsonArray.add(it)
            }
            is String -> {
                jsonArray.add(it as String)
            }
            is Number -> {
                jsonArray.add(it as Number)
            }
            else -> it.toJsonObject()?.also { obj ->
                jsonArray.add(obj)
            }
        }
    }
    if (jsonArray.isEmpty()) return null
    return jsonArray
}

fun <T> JsonObject?.parse(cls: Class<T>): T? {
    this ?: return null
    return this.toString().parse(cls)
}

fun <T> JsonArray?.parse(cls: Class<Array<T>>): List<T>? {
    this ?: return null
    return this.toString().parse(cls)
}

fun <T> String?.parse(cls: Class<T>): T? {
    if (isNullOrEmpty()) {
        return null
    }
    return try {
        return convertFactory.fromJson(this, cls)
    } catch (ignore: Exception) {
        null
    }
}

fun <T> String?.parse(cls: Class<Array<T>>): List<T>? {
    if (isNullOrEmpty()) {
        return null
    }
    return try {
        return convertFactory.fromJson(StringReader(this), cls).toList()
    } catch (ignore: Exception) {
        null
    }
}

fun readObject(fileName: String): JsonObject? {
    return readAsset(fileName).parse(JsonObject::class.java)
}

fun JsonElement?.toObject(): JsonObject? {
    this ?: return null
    if (this.isJsonNull) return null
    if (!this.isJsonObject) return null
    return this.asJsonObject
}

fun JsonArray?.isEmpty(): Boolean {
    this ?: return true
    return this.size() == 0
}

/**
 * [JsonObject]
 */
fun JsonObject.putString(key: String, value: String?): JsonObject {
    addProperty(key, value ?: "")
    return this
}

fun JsonObject.putLong(key: String, value: Long?): JsonObject {
    addProperty(key, value ?: 0L)
    return this
}

fun JsonObject.put(key: String, value: String?): JsonObject {
    if (null != value) addProperty(key, value)
    return this
}

fun JsonObject.put(key: String, value: Boolean?): JsonObject {
    if (null != value) addProperty(key, value)
    return this
}

fun JsonObject.put(key: String, value: Number?): JsonObject {
    if (null != value) addProperty(key, value)
    return this
}

fun JsonObject.put(key: String, value: JsonElement?): JsonObject {
    if (null != value) add(key, value)
    return this
}

fun <T> JsonObject.put(key: String, value: List<T?>?): JsonObject {
    if (null != value) add(key, value.toJsonArray())
    return this
}

fun JsonObject?.obj(string: String): JsonObject? {
    this ?: return null
    if (!has(string)) return null
    if (!get(string).isJsonObject) return null
    return get(string).asJsonObject
}

fun JsonObject?.array(key: String): JsonArray? {
    this ?: return null
    if (!has(key)) return null
    if (get(key).isJsonNull) return null
    if (!get(key).isJsonArray) return null
    val arr = get(key).asJsonArray
    if (arr.size() == 0) return null
    return arr
}

fun JsonObject?.list(key: String): List<JsonObject>? {
    val s = array(key).toString()
    return try {
        return convertFactory.fromJson(StringReader(s), Array<JsonObject>::class.java).toList()
    } catch (ignore: Exception) {
        null
    }
}

fun JsonObject?.str(key: String, default: String? = null): String? {
    this ?: return default
    if (!has(key)) return default
    if (get(key).isJsonNull) return default
    val s = get(key)?.asString
    return s ?: default
}

fun JsonObject?.int(key: String, default: Int = -1): Int {
    try {
        this ?: return default
        if (!has(key)) return default
        if (get(key).isJsonNull) return default
        return get(key)?.asInt ?: get(key)?.asString?.toIntOrNull()!!
    } catch (e: Exception) {
        return this!!.get(key)?.asString?.toDoubleOrNull()?.toInt() ?: 0
    }
}

fun JsonObject?.long(key: String, default: Long = -1): Long {
    try {
        this ?: return default
        if (!has(key)) return default
        if (get(key).isJsonNull) return default
        return get(key)?.asLong ?: get(key)?.asString?.toLongOrNull()!!
    } catch (e: Exception) {
        return this!!.get(key)?.asString?.toDoubleOrNull()?.toLong() ?: 0
    }
}

fun JsonObject?.decimal(key: String, default: BigDecimal = BigDecimal.ZERO): BigDecimal {
    try {
        this ?: return default
        if (!has(key)) return default
        if (get(key).isJsonNull) return default
        return get(key)?.asBigDecimal ?: get(key)?.asString?.toBigDecimalOrNull()
        ?: 0.toBigDecimal()
    } catch (e: Exception) {
        return 0.toBigDecimal()
    }
}

fun JsonObject?.float(key: String, default: Float = 0f): Float {
    try {
        this ?: return default
        if (!has(key)) return default
        if (get(key).isJsonNull) return default
        return get(key)?.asFloat ?: get(key)?.asString?.toFloatOrNull() ?: 0f
    } catch (e: Exception) {
        return 0f
    }
}







