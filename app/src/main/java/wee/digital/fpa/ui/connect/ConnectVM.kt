package wee.digital.fpa.ui.connect

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import wee.digital.fpa.ui.base.BaseViewModel

class ConnectVM : BaseViewModel() {
    val objQRCode = MutableLiveData<JsonObject>()
}
