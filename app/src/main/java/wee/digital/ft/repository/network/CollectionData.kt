package wee.digital.ft.repository.network

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
import wee.digital.ft.camera.DataCollect
import wee.digital.ft.repository.base.BaseData
import wee.digital.ft.repository.utils.SystemUrl
import java.util.concurrent.TimeUnit

class CollectionData {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CollectionData()
        }
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
    fun encryptCollData(dataCollect: DataCollect){
        EncryptData.instance.encryptCollectData(dataCollect)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe (
                        {
                            Log.e("encryptCollectData","success")
                            postCollectData(it.headers,it.body)
                        },
                        {
                            Log.e("encryptCollectData","${it.message}")
                        }
                )
    }

    @SuppressLint("CheckResult")
    private fun postCollectData(headers: HashMap<String,Any>, data: String) {
        val apiService = getClient().create(MyApiService::class.java)
        headers["FacePOSCollectData"] = "POS${BaseData.deviceInfo.shopName}_${BaseData.deviceInfo.posName}"
        apiService.postCollData(headers, data)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Response<ResponseBody>> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: Response<ResponseBody>) {
                        Log.e("facePosCollectData", "onSuccess")
                    }

                    override fun onError(e: Throwable) {
                        Log.e("facePosCollectData", "onError")
                    }

                })
    }
}