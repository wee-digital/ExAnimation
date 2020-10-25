package wee.digital.fpa.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessageVM : ViewModel() {

    val argLiveData = MutableLiveData<MessageArg>()
}