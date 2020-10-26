package wee.digital.fpa.ui

import androidx.navigation.NavDirections
import wee.digital.fpa.ui.base.EventLiveData


object Main {

    val rootDirection by lazy {
        EventLiveData<NavDirections>()
    }
}