package wee.digital.fpa.repository.network

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.repository.dto.VideoRecordData
import wee.digital.fpa.repository.dto.VideoRecordReq
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.model.EncryptResult
import wee.digital.fpa.repository.model.EncryptResultCheckDevice
import wee.digital.fpa.repository.utils.ErrCode
import wee.digital.fpa.repository.utils.SystemUrl
import wee.digital.library.extension.int
import wee.digital.library.extension.str

class Api {

    companion object {
        val instance: Api by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Api() }

        var timeIn = 0L
    }

    /**
     * for register device
     */
    @SuppressLint("CheckResult")
    fun postLogin(url: String, data: DeviceInfoStore, listener: ApiCallBack) {
        EncryptData.instance.encryptRegister(data)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<EncryptResult> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(dataEncrypt: EncryptResult) {
                        callLogin(url, dataEncrypt, listener)
                    }

                    override fun onError(e: Throwable) {
                        listener.onFail(ErrCode.ENCRYPT_DATA_FAIL, e.message.toString(), null)
                    }

                })
    }

    private fun callLogin(url: String, dataReq: EncryptResult, listener: ApiCallBack) {
        val bodyCustom = dataReq.body.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        RestClient.restApi.postApi(url, dataReq.headers, bodyCustom)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Response<ResponseBody>> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(response: Response<ResponseBody>) {
                        Log.e("dataApi", "$response")

//                        val timeReceiver = response.raw().receivedResponseAtMillis().toDouble()
//                        val timeSend = response.raw().sentRequestAtMillis().toDouble()
//                        val time = timeReceiver - timeSend

                        when (response.code()) {

                            200 -> {
                                val parseData = EncryptData.instance.decryptResponse(response)
                                if (parseData == null) {
                                    val messFail = "decrypt data response fail"
                                    listener.onFail(ErrCode.CONVERT_DATA_FAIL, messFail, null)

                                    return
                                }
                                if (parseData.int("Code") == 0) {
                                    listener.onSuccess(parseData)
                                } else {
                                    val errorCode = parseData.str("Message") ?: ""
                                    listener.onFail(parseData.int("Code"), errorCode, parseData)
                                }
                                /*LogGrafana.instance.postHttp(
                                    url,
                                    timeCall,
                                    parseData.int("Code"),
                                    parseData.string("Message")
                                )*/
                            }
                            400 -> {
                                val parseData = EncryptData.instance.decryptResponseBadRequest(response)
                                if (parseData == null) {
                                    listener.onFail(response.code(), response.message(), null)
                                } else {
                                    val errorCode = parseData.str("Message") ?: ""
                                    listener.onFail(parseData.int("Code"), errorCode, parseData)
                                }
                            }
                            else -> {
                                /* LogGrafana.instance.postHttp(
                                     url,
                                     timeCall,
                                     response.code(),
                                     response.message()
                                 )*/

                                listener.onFail(response.code(), response.message(), null)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("CallApiLog", "$e")

                        val timeCall = System.currentTimeMillis() - timeIn
                        /*LogGrafana.instance.postHttp(url, timeCall, 404, "${e.message}")*/

                        listener.onFail(ErrCode.API_FAIL, e.message.toString(), null)
                    }

                })
    }

    /**
     * for api after registration
     */
    @SuppressLint("CheckResult")
    fun <T> postApi(url: String, data: T?, header: FacePointData? = null, listener: ApiCallBack) {
        timeIn = System.currentTimeMillis()

        val strData = if (data != null) Gson().toJson(data) else null
        EncryptData.instance.encryptDataString(data = strData, dataPoint = header)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<EncryptResult> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(dataEncrypt: EncryptResult) {
                        callLogin(url, dataEncrypt, listener)
                    }

                    override fun onError(e: Throwable) {
                        listener.onFail(ErrCode.ENCRYPT_DATA_FAIL, e.message.toString(), null)
                    }

                })
    }

    /**
     * for api check device exist
     */
    fun postCheckDevice(url: String, listener: ApiCallBack) {
        EncryptData.instance.encryptDataCheckDevice()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<EncryptResultCheckDevice> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(dataEncrypt: EncryptResultCheckDevice) {
                        callApiCheckDevice(url, dataEncrypt, listener)
                    }

                    override fun onError(e: Throwable) {
                        listener.onFail(ErrCode.ENCRYPT_DATA_FAIL, e.message.toString(), null)
                    }

                })
    }

    @SuppressLint("CheckResult")
    private fun callApiCheckDevice(
            url: String,
            dataReq: EncryptResultCheckDevice,
            listener: ApiCallBack
    ) {
        val bodyCustom = dataReq.body.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        RestClient.restApi.postApi(url, dataReq.headers, bodyCustom)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Response<ResponseBody>> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(response: Response<ResponseBody>) {
                        Log.e("dataApi", "$response")

                        val timeCall = System.currentTimeMillis() - timeIn

                        Log.e("timeCallApi", "$url - [${System.currentTimeMillis() - timeIn}]")

                        when (response.code()) {

                            200 -> {
                                val parseData = EncryptData.instance.decryptResponseCheckDevice(
                                        response,
                                        dataReq.privateKey
                                )
                                if (parseData == null) {
                                    val messFail = "decrypt data response fail"
                                    listener.onFail(ErrCode.CONVERT_DATA_FAIL, messFail, null)

                                    return
                                }

                                if (parseData.int("Code") == 0) {
                                    listener.onSuccess(parseData)
                                } else {
                                    val errorCode = parseData.str("Message") ?: ""
                                    listener.onFail(parseData.int("Code"), errorCode, parseData)
                                }
                                /*LogGrafana.instance.postHttp(
                                    url,
                                    timeCall,
                                    parseData.int("Code"),
                                    parseData.string("Message")
                                )*/
                            }
                            else -> {
                                /*LogGrafana.instance.postHttp(
                                    url,
                                    timeCall,
                                    response.code(),
                                    response.message()
                                )*/

                                listener.onFail(response.code(), response.message(), null)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("CallApiLog", "$e")

                        val timeCall = System.currentTimeMillis() - timeIn
                        /*LogGrafana.instance.postHttp(url, timeCall, 404, "${e.message}")*/

                        listener.onFail(ErrCode.API_FAIL, e.message.toString(), null)
                    }

                })

    }

    /**
     * for api upload Video
     */
    fun encryptDataVideo(dataReq: VideoRecordData, listener: ApiCallBack) {
        val strData = Gson().toJson(dataReq.data)
        EncryptData.instance.encryptDataString(strData, null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<EncryptResult> {

                    override fun onSubscribe(d: Disposable) {}

                    @SuppressLint("CheckResult")
                    override fun onSuccess(encryptResp: EncryptResult) {
                        val data = VideoRecordReq(
                                Base64.encodeToString(dataReq.face, Base64.NO_WRAP),
                                Base64.encodeToString(encryptResp.body, Base64.NO_WRAP)
                        )
                        postVideo(encryptResp.headers, data, listener)
                    }

                    override fun onError(e: Throwable) {
                        listener.onFail(ErrCode.API_FAIL, e.message.toString(), null)
                        Log.e("callUpLoadVideo", "onError : ${e.message}")
                    }

                })
    }

    private fun postVideo(headers: HashMap<String, Any>, data: VideoRecordReq, listener: ApiCallBack) {
        val restApi = RestUrl(SystemUrl.BASE_URL_VIDEO).getClient().create(MyApiService::class.java)
        restApi.postVideo("v2", headers, data)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Response<ResponseBody>> {

                    override fun onSubscribe(d: Disposable) {
                        Log.e("callUpLoadVideo", "onSubscribe")
                    }

                    override fun onSuccess(t: Response<ResponseBody>) {
                        Log.e("callUpLoadVideo", "${t.code()}")
                        listener.onSuccess(JsonObject())
                    }

                    override fun onError(e: Throwable) {
                        Log.e("callUpLoadVideo", e.message.toString())
                        listener.onFail(ErrCode.API_FAIL, "${e.message}", null)
                    }

                })
    }

    interface ApiCallBack {
        fun onSuccess(data: JsonObject)
        fun onFail(code: Int, mess: String, data: JsonObject?)
    }

    interface ClientListener<T> {
        fun onSuccess(data: T)
        fun onFailed(code: Int, message: String) {}
        fun onFailed(data: T) {}
        fun onSuccess() {}
    }
}