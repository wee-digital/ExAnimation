package wee.digital.fpa.ui

import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.dto.GetTokenDTOResp
import wee.digital.fpa.repository.dto.UpdateCancelPaymentDTOReq
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.repository.utils.ErrCode
import wee.digital.fpa.ui.base.BaseViewModel
import java.util.concurrent.TimeUnit

class MainVM : BaseViewModel() {

    private var paymentRequest: Disposable? = null

    var tokenResponse = MutableLiveData<GetTokenDTOResp>()

    val deviceInfo = MutableLiveData<DeviceInfo?>()

    val rootDirection get() = Main.rootDirection

    fun syncDeviceInfo() {
        deviceInfo.postValue(BaseData.ins.getDeviceInfoPref())
    }

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
        DeviceSystemRepository.ins.getToken(object : Api.ClientListener<GetTokenDTOResp> {

            override fun onSuccess(data: GetTokenDTOResp) {
                log.d("getToken: $data")
                tokenResponse.postValue(data)
            }

            override fun onFailed(code: Int, message: String) {
                log.d("getToken: $code - $message")
                val dataFail = GetTokenDTOResp(ErrCode.GET_TOKEN_SOCKET_FAIL, "", "")
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