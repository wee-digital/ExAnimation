package wee.digital.fpa.ui

import android.content.Context
import androidx.navigation.NavDirections
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.vm.RemainingVM


class Main {

    companion object {
        val rootDirection by lazy {
            EventLiveData<NavDirections>()
        }
    }

    abstract class Fragment : BaseFragment() {

        val remainingVM by lazy { activityVM(RemainingVM::class) }

        override fun onAttach(context: Context) {
            remainingVM.stopRemaining()
            super.onAttach(context)
        }
    }

    abstract class Dialog : BaseDialog() {

        val remainingVM by lazy { activityVM(RemainingVM::class) }

        override fun onAttach(context: Context) {
            remainingVM.stopRemaining()
            super.onAttach(context)
        }
    }

}