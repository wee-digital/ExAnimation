package wee.digital.fpa.repository.payment

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.repository.base.IBase
import wee.digital.fpa.repository.dto.*
import wee.digital.fpa.repository.model.ClientResponse
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.network.LogGrafana
import wee.digital.fpa.repository.network.MyApiService
import wee.digital.fpa.repository.network.RestUrl
import wee.digital.fpa.repository.utils.ErrCode
import wee.digital.fpa.repository.utils.SystemUrl
import wee.digital.library.extension.bool
import wee.digital.library.extension.parse

class PaymentProvider : IBase.Payment {

    override fun getClientId(listener: Api.ClientListener<ClientResponse>) {
        val restApi =
                RestUrl(SystemUrl.BASE_URL_CLIENT_ID).getClient().create(MyApiService::class.java)
        restApi.getClientId()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<JsonObject> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        Log.d("getClientId", "fail : ${e.message}")
                        listener.onFailed(code = ErrCode.API_FAIL, message = e.message.toString())
                    }

                    override fun onSuccess(t: JsonObject) {
                        Log.d("getClientId", "success : $t")
                        val data = Gson().fromJson(t, ClientResponse::class.java)
                        listener.onSuccess(data)
                    }

                })
    }

    override fun requestPayment(
            data: RequestPaymentDTOReq,
            listener: Api.ClientListener<RequestPaymentDTOResp>
    ) {
        Api.instance.postApi(url = "device/payment-request", data = data, listener = object : Api.ApiCallBack {
            override fun onSuccess(data: JsonObject) {
                Log.d("requestPayment", "success : $data")
                val resp = data.parse(RequestPaymentDTOResp::class.java)!!
                listener.onSuccess(response = resp)
            }

            override fun onFail(code: Int, mess: String, data: JsonObject?) {
                Log.d("requestPayment", "fail : $code - $mess - $data")
                listener.onFailed(code = code, message = mess)
            }
        })
    }

    override fun verifyFace(
            dataReq: FaceRequest,
            facePointData: FacePointData,
            listener: Api.ClientListener<FaceResponse>
    ) {
        Api.instance.postApi(
                url = "verifyFace",
                data = dataReq,
                header = facePointData,
                listener = object : Api.ApiCallBack {

                    override fun onSuccess(data: JsonObject) {
                        Log.d("verifyFace", "$data")
                        val resp = data.parse(FaceResponse::class.java)!!
                        resp.code = ErrCode.SUCCESS

                        listener.onSuccess(resp)
                    }

                    override fun onFail(code: Int, mess: String, data: JsonObject?) {
                        Log.d("verifyFace", "$code - $mess - $data")
                        val errCode = verifyFaceFailed(code, mess, data)
                        listener.onFailed(code = errCode, message = mess)
                    }
                })
    }

    override fun verifyPINCode(
            dataReq: PinRequest,
            listener: Api.ClientListener<PinResponse>
    ) {
        Api.instance.postApi(url = "verifyPinCode", data = dataReq, listener = object : Api.ApiCallBack {
            override fun onSuccess(data: JsonObject) {
                Log.d("verifyPINCode", "$data")
                val resp = data.parse(PinResponse::class.java)!!
                resp.code = ErrCode.SUCCESS

                listener.onSuccess(resp)
            }

            override fun onFail(code: Int, mess: String, data: JsonObject?) {
                Log.d("verifyPINCode", "$code - $mess - $data")
                val errCode = verifyPINCodeFailed(code, mess, data)
                listener.onFailed(code = errCode, message = mess)
            }

        })
    }

    override fun payment(dataReq: PaymentDTOReq, listener: Api.ClientListener<PaymentResponse>) {
        Api.instance.postApi(url = "facepay", data = dataReq, listener = object : Api.ApiCallBack {

            override fun onSuccess(data: JsonObject) {
                Log.d("payment", "$data")
                val resp = data.parse(PaymentResponse::class.java)!!
                resp.code = ErrCode.SUCCESS

                listener.onSuccess(resp)
            }

            override fun onFail(code: Int, mess: String, data: JsonObject?) {
                Log.d("payment", "$code - $mess - $data")
                val resp = paymentFailed(code, mess, data)
                listener.onFailed(resp)
            }
        })
    }

    override fun getBankAccList(
            dataReq: GetBankAccListDTOReq,
            listener: Api.ClientListener<GetBankAccListDTOResp>
    ) {
        Api.instance.postApi(url = "get-bank-accounts", data = dataReq, listener = object : Api.ApiCallBack {

            override fun onSuccess(data: JsonObject) {
                Log.d("getBankAccList", "$data")
                val resp = data.parse(GetBankAccListDTOResp::class.java)!!

                listener.onSuccess(resp)
            }

            override fun onFail(code: Int, mess: String, data: JsonObject?) {
                Log.d("getBankAccList", "$code - $mess - $data")
                listener.onFailed(code = code, message = mess)
            }
        })
    }

    override fun updatePaymentStatus(dataReq: UpdatePaymentStatusDTOReq) {
        Api.instance.postApi(url = "device/update-payment-request", data = dataReq, listener = object : Api.ApiCallBack {
            override fun onSuccess(data: JsonObject) {}

            override fun onFail(code: Int, mess: String, data: JsonObject?) {
                Log.d("updatePaymentStatus", "$code - $mess - $data")
            }
        })
    }

    override fun updateCancelPayment(dataReq: UpdateCancelPaymentDTOReq) {
        Api.instance.postApi(
                url = "device/update-cancel-payment",
                data = dataReq,
                listener = object : Api.ApiCallBack {
                    override fun onSuccess(data: JsonObject) {
                        Log.d("updateCancelPayment", "$data")
                    }

                    override fun onFail(code: Int, mess: String, data: JsonObject?) {
                        Log.d("updateCancelPayment", "$code - $mess - $data")
                    }
                })
    }

    //---
    private fun verifyFaceFailed(code: Int, mess: String, data: JsonObject?): Int {
        if (code in 11..16) LogGrafana.instance.postNoAccount("$code, $mess")

        return when (code) {
            in 11..16 -> {
                if (data?.bool("ReTry") == true) {
                    ErrCode.FACE_NOT_FOUND
                } else {
                    ErrCode.MAX_FACE_VERIFICATION
                }
            }
            else -> code
        }
    }

    private fun verifyPINCodeFailed(code: Int, mess: String, data: JsonObject?): Int {
        Log.d("verifyPin", "$code - $mess - $data")

        return when (code) {
            ErrCode.API_FAIL -> ErrCode.WRONG_PIN_CODE
            else -> code
        }
    }

    private fun paymentFailed(code: Int, mess: String, data: JsonObject?): PaymentResponse {
        Log.d("payment", "$code - $mess - $data")

        val resp = PaymentResponse()
        if (code == ErrCode.INSUFFICIENT_ACC_BALANCE) {
            resp.isRetry = data.bool("ReTry", false)
            resp.code = code
        } else {
            resp.code = code
        }

        return resp
    }
}