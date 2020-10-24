package wee.digital.fpa.ui.device

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.repository.deviceSystem.DeviceSystemRepository
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.utils.FailType

class InputDeviceVM : ViewModel(){

    val statusRegister = MutableLiveData<Boolean>()

    fun registerDevice(){
        val  data : DeviceInfoStore? = Shared.deviceInfo.value
        if(data == null){
            statusRegister.postValue(false)
            return
        }
        DeviceSystemRepository.ins.register(data, object : Api.ClientListener<Any>{

            override fun onSuccess(data: Any) {
                statusRegister.postValue(true)
            }

            override fun onFailed(code: Int, message: String) {
                when(code){
                    in 1..6, 500 -> Shared.statusFailConnect.postValue(FailType.QR_FAIL)
                    else -> Shared.statusFailConnect.postValue(FailType.QR_FAIL)
                }
                BaseData.ins.resetDeviceInfo()
                statusRegister.postValue(false)
            }

        })
    }

}