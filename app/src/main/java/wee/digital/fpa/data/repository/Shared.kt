package wee.digital.fpa.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import wee.digital.fpa.repository.model.DeviceInfoStore

object Shared {

    /**
     * data for flow connect device
     */
    val deviceInfo = MutableLiveData<DeviceInfoStore>()

    /**
     * data for flow payment FacePay
     */
    val paymentID = MutableLiveData<String>()

    val clientID = MutableLiveData<String>()

    var paymentProcessing: Boolean = false

    val calledFacePay = MutableLiveData<Boolean>()

    val totalTimeFacePay = MutableLiveData<Int>()

    val amountTransaction = MutableLiveData<String>()

    val bankJson = MutableLiveData<JsonArray?>()


}