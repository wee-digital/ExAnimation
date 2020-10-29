package wee.digital.fpa.ui.progress

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.ui.base.BaseViewModel

class ProgressVM : BaseViewModel() {

    val arg = MutableLiveData<ProgressArg?>()
}