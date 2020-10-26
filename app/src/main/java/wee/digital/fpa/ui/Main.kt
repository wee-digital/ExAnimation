package wee.digital.fpa.ui

import androidx.navigation.NavDirections
import wee.digital.fpa.ui.base.EventLiveData

object Main {

    val direction by lazy { EventLiveData<NavDirections>() }

}