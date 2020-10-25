package wee.digital.fpa.repository.deviceSystem

import com.google.gson.JsonObject
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.base.IBase
import wee.digital.fpa.repository.dto.GetTokenDTOResp
import wee.digital.fpa.repository.dto.RegisterDTOResp
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.utils.ErrCode
import wee.digital.library.extension.bool
import wee.digital.library.extension.parse

class DeviceSystemProvider : IBase.DeviceSystem {

    override fun register(data: DeviceInfoStore, listener: Api.ClientListener<Any>) {
        Api.instance.postLogin("register", data, object : Api.ApiCallBack {
            override fun onSuccess(data: JsonObject) {
                val registerResp = data.parse(RegisterDTOResp::class.java)!!
                BaseData.ins.saveDeviceInfoPref(registerResp.getDeviceInfo(), registerResp.pubKey)
                listener.onSuccess(data)
            }

            override fun onFail(code: Int, mess: String, data: JsonObject?) {
                listener.onFailed(code, mess)
            }
        })
    }

    override fun checkDeviceStatus(listener: Api.ClientListener<Int>) {
        Api.instance.postCheckDevice("check-status", object : Api.ApiCallBack {
            override fun onSuccess(data: JsonObject) {
                if (data.bool("IsRemoved")) {
                    listener.onSuccess(ErrCode.DEVICE_DELETE)
                } else {
                    listener.onSuccess(ErrCode.DEVICE_EXISTS)
                }
            }

            override fun onFail(code: Int, mess: String, data: JsonObject?) {
                listener.onFailed(code, mess)
            }

        })
    }

    override fun getToken(listener: Api.ClientListener<GetTokenDTOResp>) {
        Api.instance.postApi(url = "getToken", data = null, listener = object : Api.ApiCallBack {
            override fun onSuccess(data: JsonObject) {
                val resp = data.parse(GetTokenDTOResp::class.java)!!
                listener.onSuccess(resp)
            }

            override fun onFail(code: Int, mess: String, data: JsonObject?) {
                listener.onFailed(code, mess)
            }
        })
    }
}