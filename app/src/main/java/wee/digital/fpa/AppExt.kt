package wee.digital.example

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import wee.digital.library.Library
import wee.digital.library.extension.SECOND
import java.text.SimpleDateFormat

fun App.onModulesInject() {
    Library.app = this
}

val appIsDev get() = BuildConfig.FLAVOR == "dev"

var appIsDebug = BuildConfig.DEBUG

const val appId = BuildConfig.APPLICATION_ID

const val roomVersion: Int = 1

val appInfo = """
            ${BuildConfig.FLAVOR} (${BuildConfig.BUILD_TYPE})
            ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
        """.trimIndent()

const val networkTimeout = 90 * SECOND

val dateFormat = SimpleDateFormat("dd/MM/yyyy")

val dateTimeFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")

fun toast(message: String?) {
    message ?: return
    if (Looper.myLooper() == Looper.getMainLooper()) {
        Toast.makeText(app, message, Toast.LENGTH_SHORT).show()
    } else Handler(Looper.getMainLooper()).post {
        Toast.makeText(app, message, Toast.LENGTH_SHORT).show()
    }
}