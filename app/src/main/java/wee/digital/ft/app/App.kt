package wee.digital.ft.app

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonArray
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import wee.digital.ft.BuildConfig
import wee.digital.ft.camera.RealSenseControl
import wee.digital.ft.repository.base.BaseSharedPref
import wee.digital.ft.repository.network.HttpsTrustManager
import wee.digital.ft.repository.network.MyApiService
import wee.digital.ft.repository.network.RestUrl
import wee.digital.ft.repository.utils.SystemUrl
import wee.digital.ft.shared.Event
import wee.digital.ft.shared.Shared
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
        Event.initConsumer()
        HttpsTrustManager.allowAllSSL()
        baseSharedPref = BaseSharedPref().also {
            // TODO: loi khi config release
            it.init(this)
        }
        getBanksJson()

    }

    companion object {
        var realSenseControl: RealSenseControl? = null

        var baseSharedPref: BaseSharedPref? = null

        /* var recordVideo : MyVideo? = null*/
    }

    /**
     * get banks json when open app
     */
    private fun getBanksJson() {
        val restApi = RestUrl(SystemUrl.URL_GET_BANKS).getClient().create(MyApiService::class.java)
        restApi.getBanksJson()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Response<JsonArray>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(response: Response<JsonArray>) {
                        Log.d("MyAppGetBanks", response.body().toString())
                        if (response.code() == 200) {
                            Shared.bankJson.postValue(response.body())
                        } else {
                            Shared.bankJson.postValue(null)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.d("MyAppGetBanks", e.message.toString())
                        Shared.bankJson.postValue(null)
                    }
                })
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
