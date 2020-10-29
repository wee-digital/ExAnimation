package wee.digital.fpa.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import wee.digital.fpa.repository.model.DeviceInfoStore

object Shared {

    /**
     * data for flow payment FacePay
     */
    var paymentProcessing: Boolean = false

    val calledFacePay = MutableLiveData<Boolean>()

    val totalTimeFacePay = MutableLiveData<Int>()

    val amountTransaction = MutableLiveData<String>()

    val bankJson = MutableLiveData<JsonArray?>()


}