package wee.digital.fpa.ui

import androidx.navigation.NavDirections
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.EventLiveData


class Main {

    companion object {
        val rootDirection by lazy {
            EventLiveData<NavDirections>()
        }
    }

    abstract class Fragment : BaseFragment() {

    }

    abstract class Dialog : BaseDialog() {

    }
}