package wee.digital.ft.ui

import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import crypto.Crypto
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import wee.digital.ft.repository.base.BaseData
import wee.digital.ft.repository.deviceSystem.DeviceSystemRepository
import wee.digital.ft.repository.dto.InfoVideoReq
import wee.digital.ft.repository.dto.TokenResponse
import wee.digital.ft.repository.dto.VideoRecordData
import wee.digital.ft.repository.network.Api
import wee.digital.ft.repository.utils.ErrCode
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.library.extension.restartApp
import java.io.File
import java.util.concurrent.TimeUnit

class MainVM : BaseViewModel() {

    private var paymentRequest: Disposable? = null

    var tokenResponse = MutableLiveData<TokenResponse>()

    fun checkDeviceStatusOnTimer() {
        paymentRequest = Single.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkDeviceStatus()
                }, {

                })

    }

    fun checkDeviceStatus() {
        DeviceSystemRepository.ins.checkDeviceStatus(object : Api.ClientListener<Int> {

            override fun onSuccess(data: Int) {
                log.d("checkDeviceStatus: $data")
                onDeviceStatus(data)
            }

            override fun onFailed(code: Int, message: String) {
                log.d("checkDeviceStatus $code - $message")
                checkDeviceStatusOnTimer()
            }

        })
    }

    private fun getToken() {
        DeviceSystemRepository.ins.getToken(object : Api.ClientListener<TokenResponse> {

            override fun onSuccess(response: TokenResponse) {
                log.d("getToken: $response")
                tokenResponse.postValue(response)
            }

            override fun onFailed(code: Int, message: String) {
                log.d("getToken: $code - $message")
                val dataFail = TokenResponse(ErrCode.GET_TOKEN_SOCKET_FAIL, "", "")
                tokenResponse.postValue(dataFail)
            }

        })
    }

    fun resetDeviceData() {
        BaseData.ins.resetDeviceInfo()
        restartApp()
    }

    private fun onDeviceStatus(status: Int) {
        when (status) {
            ErrCode.DEVICE_EXISTS -> {
                getToken()
            }
            ErrCode.DEVICE_DELETE -> {
                resetDeviceData()
            }
        }
    }

    fun pushVideo(path: String, paymentID: String) {
        if (path.isEmpty()) return
        Single.fromCallable { getVideoToPath(path) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ByteArray> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(video: ByteArray) {
                        Log.e("pushVideo", "success")
                        val info = InfoVideoReq(
                                paymentID = paymentID,
                                hashedFace = Crypto.hashPwd(Base64.encodeToString(video, Base64.NO_WRAP))
                        )
                        val req = VideoRecordData(face = video, data = info)
                        Api.instance.encryptDataVideo(req)
                    }

                    override fun onError(e: Throwable) {
                        Log.e("pushVideo", "fail : ${e.message}")
                    }

                })
    }

    private fun getVideoToPath(path: String): ByteArray {
        return File(path).readBytes()
    }

}