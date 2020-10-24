package wee.digital.fpa.data.repository

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.repository.model.DeviceInfoStore

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: ExAnimation
 * @Created: Huy 2020/10/23
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
object Shared {

    val deviceInfo = MutableLiveData<DeviceInfoStore>()

}