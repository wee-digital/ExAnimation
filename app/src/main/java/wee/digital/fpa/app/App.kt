package wee.digital.fpa.app

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import wee.digital.fpa.BuildConfig
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.data.shared
import wee.digital.library.Library
import wee.digital.library.extension.SECOND
import wee.digital.log.LogBook
import java.text.SimpleDateFormat

lateinit var app: App private set

class App : Application() {

    /**
     * [Application] override
     */
    override fun onCreate() {
        super.onCreate()
        app = this
        app.onModulesInject()
    }

    companion object{
        var realSenseControl : RealSenseControl? = null
    }

}

fun App.onModulesInject() {
    Library.app = this
    LogBook.app = this
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
