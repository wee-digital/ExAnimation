package wee.digital.fpa.ui.confirm

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.ui.base.BaseViewModel

class ConfirmVM : BaseViewModel() {

    val arg = MutableLiveData<ConfirmArg>()
}