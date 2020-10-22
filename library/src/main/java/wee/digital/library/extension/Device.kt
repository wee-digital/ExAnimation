package wee.digital.library.extension

import android.app.Application
import android.os.Build
import android.provider.Settings
import wee.digital.library.Library
import java.net.NetworkInterface
import java.util.*

private val app: Application get() = Library.app

val macAddress: String
    get() {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (e: Exception) {
        }
        return ""
    }

val androidId: String
    get() {
        return Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
    }

val deviceModel: String
    get() {
        return if (Build.MODEL.startsWith(Build.MANUFACTURER)) {
            Build.MODEL.capitalize()
        } else {
            Build.MODEL
        }
    }

val deviceName: String
    get() {
        return if (Build.MODEL.startsWith(Build.MANUFACTURER)) {
            Build.MODEL.capitalize()
        } else {
            Build.MANUFACTURER.capitalize() + " " + Build.MODEL
        }
    }