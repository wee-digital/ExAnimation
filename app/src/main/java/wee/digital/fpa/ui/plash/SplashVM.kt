package wee.digital.fpa.ui.plash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject

class SplashVM : ViewModel() {

    val paymentInfo = MutableLiveData<JsonObject?>()


}