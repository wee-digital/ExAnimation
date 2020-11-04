package wee.digital.ft.repository.network

import android.annotation.SuppressLint
import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import wee.digital.ft.repository.base.BaseData
import wee.digital.ft.repository.model.LogStreams
import wee.digital.ft.repository.model.MyStream
import wee.digital.ft.repository.utils.SystemUrl

class LogGrafana {

    companion object {
        val instance: LogGrafana by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { LogGrafana() }
    }

    private var retrofit: Retrofit? = null

    private fun getClient(): Retrofit {
        if (retrofit != null) return retrofit as Retrofit
        retrofit = Retrofit.Builder()
                .baseUrl(SystemUrl.BASE_URL_LOG)
                .client(OkHttpClient().newBuilder().build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit as Retrofit
    }

    @SuppressLint("CheckResult")
    fun postWebSocket(valueData: String) {
        if (BaseData.deviceInfo.shopName.isEmpty()) return
        val apiService = getClient().create(MyApiService::class.java)
        val headers: HashMap<String, String> = HashMap()
        headers["facePOSWebSocket"] = "POS${BaseData.deviceInfo.shopName}_${BaseData.deviceInfo.posName}"
        val arrValue = arrayOf("${System.currentTimeMillis()}000000", Gson().toJson(valueData))
        val streamData = MyStream(stream = headers, values = arrayOf(arrValue))
        val pushDataLog = LogStreams(arrayListOf(streamData))
        apiService.postLog(pushDataLog)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        { data ->
                            print("")
                        },
                        { e ->
                            print("")
                        }
                )
    }

    @SuppressLint("CheckResult")
    fun postNoAccount(valueData: String) {
        if (BaseData.deviceInfo.shopName.isEmpty()) return
        val apiService = getClient().create(MyApiService::class.java)
        val data = "${SystemUrl.API_URL}_${BaseData.deviceInfo.posName}_POS($valueData)"
        val headers: HashMap<String, String> = HashMap()
        headers["facePOSWebSocket"] = "result_no_face"
        val arrValue = arrayOf("${System.currentTimeMillis()}000000", Gson().toJson(data))
        val streamData = MyStream(stream = headers, values = arrayOf(arrValue))
        val pushDataLog = LogStreams(arrayListOf(streamData))
        apiService.postLog(pushDataLog)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        { data ->
                            print("")
                        },
                        { e ->
                            print("")
                        }
                )
    }

    @SuppressLint("CheckResult")
    fun postHttp(url: String, time: String, code: Int, fail: String? = "") {
        if (BaseData.deviceInfo.shopName.isEmpty()) return
        val apiService = getClient().create(MyApiService::class.java)
        val dataString = "URL : $url - timeCallApi : [$time] - code : $code , $fail"
        val header: HashMap<String, String> = HashMap()
        header["facePOSHttp"] = "POSHttp_${BaseData.deviceInfo.shopName}_${BaseData.deviceInfo.posName}"
        val arrValue = arrayOf("${System.currentTimeMillis()}000000", Gson().toJson(dataString))
        val streamData = MyStream(stream = header, values = arrayOf(arrValue))
        val streams = LogStreams(arrayListOf(streamData))
        apiService.postLog(streams)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        { data ->
                            print("")
                        },
                        { e ->
                            print("")
                        }
                )
    }

}
