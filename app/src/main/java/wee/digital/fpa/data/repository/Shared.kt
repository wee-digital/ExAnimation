package wee.digital.fpa.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import wee.digital.fpa.repository.model.DeviceInfoStore

object Shared {

    val qrCode = MutableLiveData<JsonObject>()

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