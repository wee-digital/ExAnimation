package wee.digital.fpa.ui

import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.dto.GetTokenDTOResp
import wee.digital.fpa.repository.dto.SocketResultResp
import wee.digital.fpa.repository.dto.UpdateCancelPaymentDTOReq
import wee.digital.fpa.repository.dto.UpdatePaymentStatusDTOReq
import wee.digital.fpa.repository.model.ClientIDResp
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.repository.utils.ErrCode
import wee.digital.fpa.repository.utils.PaymentStatusCode
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.arg.PaymentArg
import java.util.concurrent.TimeUnit

class MainVM : BaseViewModel() {

    private var paymentRequest: Disposable? = null

    var tokenResponse = MutableLiveData<GetTokenDTOResp>()

    val deviceInfo = MutableLiveData<DeviceInfo?>()

    val rootDirection get() = Main.rootDirection

    val paymentArg = MutableLiveData<PaymentArg?>()

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

    fun requestCancelPayment(type: Int) {
        val dataReq = UpdateCancelPaymentDTOReq(type = type)
        PaymentRepository.ins.updateCancelPayment(dataReq)
    }

    fun getNapasClient(dataSocket: SocketResultResp) {
        PaymentRepository.ins.getClientId(object : Api.ClientListener<ClientIDResp> {

            override fun onSuccess(data: ClientIDResp) {
                log.d("getNapasClient: $data")
                if (Shared.paymentProcessing) {
                    updateStatusPayment(dataSocket.paymentId, PaymentStatusCode.DEVICE_PROCESSING)
                    Shared.paymentProcessing = true
                    return
                }
                paymentArg.postValue(PaymentArg(
                        clientIp = data.ip,
                        paymentId = dataSocket.paymentId,
                        amount = dataSocket.amount,
                        timeout = dataSocket.timeOut
                ))
            }

            override fun onFailed(code: Int, message: String) {
                log.d("getNapasClient: $code $message")
                paymentArg.postValue(null)
            }

        })
    }

    fun resetDeviceData() {
        BaseData.ins.resetDeviceInfo()
    }

    /**
     * device status features
     */
    fun updateStatusPayment(id: String, status: Int) {
        val dataReq = UpdatePaymentStatusDTOReq(paymentID = id, status = status)
        PaymentRepository.ins.updatePaymentStatus(dataReq)
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