package wee.digital.fpa.data.repository

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.utils.FailType

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

    /**
     * data for flow connect device
     */
    val deviceInfo = MutableLiveData<DeviceInfoStore>()

    val statusFailConnect = MutableLiveData<FailType>()

    /**
     * data for flow payment FacePay
     */
    val paymentID = MutableLiveData<String>()

    val clientID = MutableLiveData<String>()

    var paymentProcessing : Boolean = false

    val calledFacePay = MutableLiveData<Boolean>()

    val totalTimeFacePay = MutableLiveData<Int>()

    val amountTransaction = MutableLiveData<String>()

}