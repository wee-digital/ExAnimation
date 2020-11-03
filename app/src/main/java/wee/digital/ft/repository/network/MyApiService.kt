package wee.digital.ft.repository.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import wee.digital.ft.repository.dto.VideoRecordReq
import wee.digital.ft.repository.model.LogStreams

@JvmSuppressWildcards
interface MyApiService {

    @POST
    fun postApi(
            @Url url: String,
            @HeaderMap headers: Map<String, Any>,
            @Body body: RequestBody
    ): Single<Response<ResponseBody>>

    @POST
    fun postVideo(
            @Url url: String,
            @HeaderMap headers: Map<String, Any>,
            @Body body: VideoRecordReq
    ): Single<Response<ResponseBody>>

    @Headers("Content-Type: application/json")
    @POST("push")
    fun postLog(@Body streams: LogStreams): Single<Response<ResponseBody>>

    @GET("banks.json")
    fun getBanksJson(): Single<Response<JsonArray>>

    @GET("get-ip")
    fun getClientId(): Single<JsonObject>

    @POST("depth/upload")
    fun postCollData(
            @HeaderMap headers: Map<String, Any>,
            @Body data: String
    ): Single<Response<ResponseBody>>

}