package wee.digital.fpa.ui.plash

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import wee.digital.fpa.ui.base.BaseViewModel

class SplashVM : BaseViewModel() {

    val paymentInfo = MutableLiveData<JsonObject?>()


}