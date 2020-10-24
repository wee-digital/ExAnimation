package wee.digital.fpa.repository.network

import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import wee.digital.fpa.repository.utils.SystemUrl
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.X509TrustManager


class RestClient {

    companion object {
        val restApi: MyApiService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RestClient().getClient().create(MyApiService::class.java)
        }
    }

    private var retrofit: Retrofit? = null

    private val timeOut = 10L

    private lateinit var okHttpClient: OkHttpClient

    fun getClient(): Retrofit {

        if (retrofit == null) initOkHttp()

        retrofit = Retrofit.Builder()
            .baseUrl(SystemUrl.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit as Retrofit
    }

    private fun initOkHttp() {

        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        okHttpClient = httpClient.build()
        /*val trustManager = HttpsTrustManager.trustManager as X509TrustManager
        val sslSocketFactory = HttpsTrustManager.sslContext!!.socketFactory
        okHttpClient = httpClient
            .sslSocketFactory(sslSocketFactory,trustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS))
            .build()*/
    }

}