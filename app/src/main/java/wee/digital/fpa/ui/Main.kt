package wee.digital.fpa.ui

import androidx.navigation.NavDirections
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.message.MessageArg

object Main {

    val direction by lazy { EventLiveData<NavDirections>() }

    val messageArg by lazy { EventLiveData<MessageArg>() }

    val confirmArg by lazy { EventLiveData<ConfirmArg>() }

}