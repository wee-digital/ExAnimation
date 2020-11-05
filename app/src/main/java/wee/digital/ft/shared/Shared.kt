package wee.digital.ft.shared

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import wee.digital.library.extension.bold
import wee.digital.library.extension.color

object Shared {

    val facePayText get() = "Facepay".bold().color("#3082D8")

    /**
     * data for flow payment FacePay
     */
    var paymentProcessing: Boolean = false

    val calledFacePay = MutableLiveData<Boolean>()

    val totalTimeFacePay = MutableLiveData<Int>()

    val amountTransaction = MutableLiveData<String>()

    val bankJson = MutableLiveData<JsonArray?>()


}