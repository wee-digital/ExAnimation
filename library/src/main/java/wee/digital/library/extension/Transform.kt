package wee.digital.library.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.math.BigDecimal

fun <T> nonNull(block: (T) -> Unit): (T?) -> Unit {
    return {
        if (it != null) block(it)
    }
}

fun ByteArray?.encodeToString(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

fun String?.decodeToBytes(): ByteArray? {
    return Base64.decode(this, Base64.NO_WRAP)
}

fun BigDecimal?.isNullOrZero(): Boolean {
    return this == null || this == BigDecimal.ZERO
}

fun ByteArray?.decodeToBitmap(): Bitmap? {
    this ?: return null
    return BitmapFactory.decodeByteArray(this, 0, size)
}

