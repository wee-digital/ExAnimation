package wee.digital.fpa.ui.activity

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.dto.GetTokenDTOResp
import wee.digital.fpa.repository.dto.SocketResultResp
import wee.digital.fpa.repository.dto.UpdateCancelPaymentDTOReq
import wee.digital.fpa.repository.dto.UpdatePaymentStatusDTOReq
import wee.digital.fpa.repository.model.ClientIDResp
import wee.digital.fpa.repository.model.DataPaymentRequest
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.repository.utils.ErrCode
import wee.digital.fpa.ui.base.BaseViewModel

class HomeVM : BaseViewModel() {

    /**
     * checkDevice
     */
    var _checkDeviceStatusCallBack = MutableLiveData<Int>()

    @SuppressLint("CheckResult")
    fun checkDeviceStatus() {
        DeviceSystemRepository.ins.checkDeviceStatus(object : Api.ClientListener<Int> {

            override fun onSuccess(data: Int) {
                Log.e("checkDeviceStatus", "success : $data")
                _checkDeviceStatusCallBack.postValue(data)
            }

            override fun onFailed(code: Int, message: String) {
                Log.e("checkDeviceStatus", "$code - $message")
                _checkDeviceStatusCallBack.postValue(ErrCode.DEVICE_FAIL)
            }

        })
    }

    /**
     * [wee.dev.faceposv2.repository.network.MyApiService] getToken
     */
    var _getTokenDTOCallBack = MutableLiveData<GetTokenDTOResp>()

    fun getToken() {
        DeviceSystemRepository.ins.getToken(object : Api.ClientListener<GetTokenDTOResp> {

            override fun onSuccess(data: GetTokenDTOResp) {
                Log.e("getToken", "success : $data")
                _getTokenDTOCallBack.postValue(data)
            }

            override fun onFailed(code: Int, message: String) {
                Log.e("getToken", "$code - $message")
                val dataFail = GetTokenDTOResp(ErrCode.GET_TOKEN_SOCKET_FAIL, "", "")
                _getTokenDTOCallBack.postValue(dataFail)
            }

        })
    }

    /**
     * updatePaymentStatus
     */
    fun updateStatusPayment(paymentID: String, paymentStatus: Int) {
        val dataReq = UpdatePaymentStatusDTOReq(paymentID = paymentID, status = paymentStatus)
        PaymentRepository.ins.updatePaymentStatus(dataReq)
    }

    /**
     * updateCancelPayment
     */
    fun updateCancelPayment(type: Int) {
        val dataReq = UpdateCancelPaymentDTOReq(type = type)
        PaymentRepository.ins.updateCancelPayment(dataReq)
    }

    /**
     * getClientID payment napas
     */
    val _clientIdCallBack = MutableLiveData<DataPaymentRequest?>()

    fun getClientID(dataSocket: SocketResultResp) {
        PaymentRepository.ins.getClientId(object : Api.ClientListener<ClientIDResp> {

            override fun onSuccess(data: ClientIDResp) {
                Log.e("getClientID", "success : $data")
                val result = DataPaymentRequest(
                        clientIp = data.ip,
                        socket = dataSocket
                )
                _clientIdCallBack.postValue(result)
            }

            override fun onFailed(code: Int, message: String) {
                Log.e("getClientID", "fail : $code $message")
                _clientIdCallBack.postValue(null)
            }

        })
    }

    /**
     * resetData when device delete
     */
    fun resetDeviceData() {
        BaseData.ins.resetDeviceInfo()
    }

}