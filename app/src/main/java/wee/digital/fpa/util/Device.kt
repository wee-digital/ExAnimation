package wee.digital.fpa.util

import android.content.res.Resources
import android.provider.Settings
import wee.digital.fpa.app.app
import wee.digital.library.extension.nowInMillis
import java.io.File

val deviceId: String
    get() {
        val id = try {
            Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: java.lang.Exception) {
            ""
        }
        val time = nowInMillis.toString()
        val timeSub = if (time.length > 4) time.substring(time.length - 4, time.length) else time
        return "$id$timeSub"
    }

val screenHeight
    get() = Resources.getSystem().displayMetrics.heightPixels


fun deleteCache() {
    try {
        val dir = app.cacheDir
        deleteDir(dir)
    } catch (e: java.lang.Exception) {
    }
}

private fun deleteDir(dir: File?): Boolean {
    return if (dir != null && dir.isDirectory) {
        val children = dir.list()
        for (i in children.indices) {
            val success = deleteDir(File(dir, children[i]))
            if (!success) {
                return false
            }
        }
        dir.delete()
    } else if (dir != null && dir.isFile) {
        dir.delete()
    } else {
        false
    }
}
