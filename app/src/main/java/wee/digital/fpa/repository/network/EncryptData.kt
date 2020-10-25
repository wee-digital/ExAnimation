package wee.digital.fpa.repository.network

import android.annotation.SuppressLint
import android.os.Build
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import crypto.Crypto
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.dto.CheckDeviceStatusDTOReq
import wee.digital.fpa.repository.dto.RegisterDTOReq
import wee.digital.fpa.repository.model.*
import wee.digital.fpa.repository.utils.Key
import wee.digital.fpa.repository.utils.SharedPrefUtil
import wee.digital.fpa.util.Utils

class EncryptData {

    companion object {
        val instance: EncryptData by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { EncryptData() }
    }

    /**
     * for encrypt register device
     */
    @SuppressLint("CheckResult")
    fun encryptRegister(
            dataReq: DeviceInfoStore
    ): Single<EncryptResult> {
        return Single.create { singleEmitter ->
            try {
                val decryptQR = FrameUtil.decryptQRCode(dataReq.qrCode)
                val sessionId = decryptQR!!.get("SessionID").asString
                val rsaKey = Crypto.rsaGenerateKey()
                SharedPrefUtil.savePriKey(rsaKey.privateKey)
                val androidId = Utils.getIdDevice()
                val bodyJson = Gson().toJson(
                        RegisterDTOReq(
                                uid = androidId,
                                pubKey = Base64.encodeToString(rsaKey.publicKey, Base64.NO_WRAP),
                                name = dataReq.name,
                                modelDevice = Build.MODEL
                        )
                )
                val bodyEncrypted = Crypto.aesEncryptCBC(
                        Base64.decode(Key.PREPARE_KEY, Base64.NO_WRAP),
                        bodyJson.toByteArray()
                )
                val headers = HashMap<String, Any>()
                headers["sessionId"] = sessionId
                singleEmitter.onSuccess(EncryptResult(headers, bodyEncrypted))
            } catch (e: Exception) {
                singleEmitter.onError(e)
            }
        }
    }

    /**
     * for encrypt data after register, data verify Face
     */
    fun encryptDataString(data: String?, dataPoint: FacePointData?): Single<EncryptResult> {
        return Single.create { singleEmitter ->
            try {
                val aesKey = Crypto.aesGenerate256Key()
                val dataFaceStr = Utils.formatDataFaceHeader(dataPoint)
                val headers = createHeader(
                        aesKey = aesKey,
                        pubKey = SharedPrefUtil.getPubKey(),
                        headerFace = dataFaceStr
                )
                val body = data?.toByteArray()
                val bodyEncrypted = createEncryptBody(aesKey, body)
                singleEmitter.onSuccess(EncryptResult(headers, bodyEncrypted!!))
            } catch (e: java.lang.Exception) {
                singleEmitter.onError(e)
            }
        }
    }

    /**
     * for encrypt data check device exists
     */
    fun encryptDataCheckDevice(): Single<EncryptResultCheckDevice> {
        return Single.create { singleEmitter ->
            try {
                val rsaKey = Crypto.rsaGenerateKey()
                val bodyJson = Gson().toJson(
                        CheckDeviceStatusDTOReq(
                                posID = BaseData.deviceInfo.uid,
                                pubKey = Base64.encodeToString(rsaKey.publicKey, Base64.NO_WRAP)
                        )
                )
                val bodyEncrypted = Crypto.aesEncryptCBC(
                        Base64.decode(Key.PREPARE_KEY, Base64.NO_WRAP),
                        bodyJson?.toByteArray()
                )
                val headers = HashMap<String, Any>()
                singleEmitter.onSuccess(EncryptResultCheckDevice(headers, bodyEncrypted!!, rsaKey.privateKey))
            } catch (e: java.lang.Exception) {
                singleEmitter.onError(e)
            }
        }
    }

    /**
     * for encrypt data after register, data verify Face
     */
    fun encryptCollectData(dataCollect: DataCollect): Single<EncryptResultCollect> {
        return Single.create { singleEmitter ->
            try {
                val data =
                        Gson().toJson(FrameUtil.repairCollectData(dataCollect)).toByteArray()
                val aesKey = Crypto.aesGenerate256Key()
                val bodyEncrypt = Crypto.aesEncryptCBC(aesKey, data)
                val weeKey = Crypto.rsaEncryptOAEP(
                        Base64.decode(Key.RSA_COLLECTDATA, Base64.NO_WRAP),
                        aesKey
                )
                val headers = HashMap<String, Any>()
                headers["weekey"] = Base64.encodeToString(weeKey, Base64.NO_WRAP)
                singleEmitter.onSuccess(
                        EncryptResultCollect(headers, Base64.encodeToString(bodyEncrypt, Base64.NO_WRAP))
                )
            } catch (e: java.lang.Exception) {
                Log.e("TestKey", "${e.message}")
                singleEmitter.onError(e)
            }
        }
    }

    private fun createHeader(
            aesKey: ByteArray,
            pubKey: ByteArray?,
            headerFace: String? = null
    ): HashMap<String, Any> {
        val enAesKey = Crypto.rsaEncryptOAEP(pubKey, aesKey)
        val header = HashMap<String, Any>()
        //---
        if (BaseData.deviceInfo.uid != "") {
            header["uid"] = BaseData.deviceInfo.uid
            header["weekey"] = Base64.encodeToString(enAesKey, Base64.NO_WRAP)
            if (headerFace != null) header["datapoint"] = headerFace
        }
        return header
    }

    private fun createEncryptBody(aesKey: ByteArray, body: ByteArray?): ByteArray? {
        val dataBody =
                body ?: Gson().toJson(ObjectDefault(uid = BaseData.deviceInfo.uid))
                        .toByteArray()
        return try {
            Crypto.aesEncryptCBC(aesKey, dataBody)
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun decryptResponse(response: Response<ResponseBody>): JsonObject? {
        return try {
            val weekeyHeader = response.headers()["weekey"]
            val weekey = Base64.decode(weekeyHeader, Base64.NO_WRAP)
            val body = response.body()!!.bytes()
            val privateKey = SharedPrefUtil.getPriKey()
            val aesKey = Crypto.rsaDecryptOAEP(weekey, privateKey)
            val bodyDecrypt = Crypto.aesDecryptCBC(aesKey, body)
            val bodyString = String(bodyDecrypt)
            val data = Gson().fromJson(bodyString, JsonObject::class.java)
            data
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun decryptResponseCheckDevice(response: Response<ResponseBody>, privateKey: ByteArray): JsonObject? {
        return try {
            val weekeyHeader = response.headers()["weekey"]
            val weekey = Base64.decode(weekeyHeader, Base64.NO_WRAP)
            val body = response.body()!!.bytes()
            val aesKey = Crypto.rsaDecryptOAEP(weekey, privateKey)
            val bodyDecrypt = Crypto.aesDecryptCBC(aesKey, body)
            val bodyString = String(bodyDecrypt)
            val data = Gson().fromJson(bodyString, JsonObject::class.java)
            data
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun decryptResponseBadRequest(response: Response<ResponseBody>): JsonObject? {
        return try {
            val body = response.errorBody()!!.bytes()
            val keyDecrypt = Base64.decode(Key.PREPARE_KEY, Base64.NO_WRAP)
            val bodyDecrypt = Crypto.aesDecryptCBC(keyDecrypt, body)
            val bodyString = String(bodyDecrypt)
            val data = Gson().fromJson(bodyString, JsonObject::class.java)
            data
        } catch (e: java.lang.Exception) {
            null
        }
    }

}