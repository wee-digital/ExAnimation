package wee.digital.fpa.repository.deviceSystem

import wee.digital.fpa.repository.dto.GetTokenDTOResp
import wee.digital.fpa.repository.base.IBase
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api

class DeviceSystemRepository: IBase.DeviceSystem{

    private val mDeviceSystemProvider = DeviceSystemProvider()

    companion object {
        val ins: DeviceSystemRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            DeviceSystemRepository()
        }
    }

    override fun register(data: DeviceInfoStore, listener: Api.ClientListener<Any>) {
        mDeviceSystemProvider.register(data = data, listener = listener)
    }

    override fun checkDeviceStatus(listener: Api.ClientListener<Int>) {
        mDeviceSystemProvider.checkDeviceStatus(listener = listener)
    }

    override fun getToken(listener: Api.ClientListener<GetTokenDTOResp>) {
        mDeviceSystemProvider.getToken(listener = listener)
    }
}