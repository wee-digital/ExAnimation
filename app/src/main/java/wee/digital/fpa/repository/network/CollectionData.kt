package wee.digital.fpa.repository.network

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.utils.SystemUrl
import java.util.concurrent.TimeUnit

class CollectionData {
    companion object {
        val instance: CollectionData by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CollectionData() }
    }

    private var retrofit: Retrofit? = null
    private fun getClient(): Retrofit {
        if (retrofit != null) return retrofit as Retrofit
        retrofit = Retrofit.Builder()
                .baseUrl(SystemUrl.BASE_URL_COLLECT)
                .client(OkHttpClient().newBuilder().callTimeout(10, TimeUnit.SECONDS).build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit as Retrofit
    }

    @SuppressLint("CheckResult")
    fun encryptCollData(dataCollect: DataCollect) {
        EncryptData.instance.encryptCollectData(dataCollect)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(
                        {
                            Log.d("encryptCollectData", "success")
                            postCollectData(it.headers, it.body)
                        },
                        {
                            Log.d("encryptCollectData", "${it.message}")
                        }
                )
    }

    @SuppressLint("CheckResult")
    private fun postCollectData(headers: HashMap<String, Any>, data: String) {
        val apiService = getClient().create(MyApiService::class.java)
        headers["FacePOSCollectData"] = "POS_${BaseData.deviceInfo.posName}"
        apiService.postCollData(headers, data)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Response<ResponseBody>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(t: Response<ResponseBody>) {
                        Log.d("facePosCollectData", "onSuccess")
                    }

                    override fun onError(e: Throwable) {
                        Log.d("facePosCollectData", "onError")
                    }
                })
    }
}