package wee.digital.ft.repository.deviceSystem

import wee.digital.ft.repository.base.IBase
import wee.digital.ft.repository.dto.TokenResponse
import wee.digital.ft.repository.model.DeviceInfoStore
import wee.digital.ft.repository.network.Api

class DeviceSystemRepository : IBase.DeviceSystem {

    private val mDeviceSystemProvider = DeviceSystemProvider()

    companion object {
        val ins: DeviceSystemRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DeviceSystemRepository()
        }
    }

    override fun register(data: DeviceInfoStore, listener: Api.ClientListener<Any>) {
        mDeviceSystemProvider.register(data = data, listener = listener)
    }

    override fun checkDeviceStatus(listener: Api.ClientListener<Int>) {
        mDeviceSystemProvider.checkDeviceStatus(listener = listener)
    }

    override fun getToken(listener: Api.ClientListener<TokenResponse>) {
        mDeviceSystemProvider.getToken(listener = listener)
    }
}