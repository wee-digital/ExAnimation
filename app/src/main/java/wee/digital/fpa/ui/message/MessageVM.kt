package wee.digital.fpa.ui.message

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.ui.base.BaseViewModel

class MessageVM : BaseViewModel() {

    val arg = MutableLiveData<MessageArg>()
}