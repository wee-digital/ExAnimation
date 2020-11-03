package wee.digital.ft.ui

import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wee.digital.ft.repository.base.BaseData
import wee.digital.ft.repository.deviceSystem.DeviceSystemRepository
import wee.digital.ft.repository.dto.TokenResponse
import wee.digital.ft.repository.network.Api
import wee.digital.ft.repository.utils.ErrCode
import wee.digital.ft.ui.base.BaseViewModel
import java.util.concurrent.TimeUnit

class MainVM : BaseViewModel() {

    private var paymentRequest: Disposable? = null

    var tokenResponse = MutableLiveData<TokenResponse>()


    val rootDirection get() = Main.mainDirection


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


}