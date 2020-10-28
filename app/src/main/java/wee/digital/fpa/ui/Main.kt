package wee.digital.fpa.ui

import android.content.Context
import androidx.navigation.NavDirections
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.vm.TimeoutVM


class Main {

    companion object {
        val rootDirection by lazy {
            EventLiveData<NavDirections>()
        }
    }

    abstract class Fragment : BaseFragment() {

        val mainVM by lazy { activityVM(MainVM::class) }

        val remainingVM by lazy { activityVM(TimeoutVM::class) }

        override fun onAttach(context: Context) {
            remainingVM.stopTimeout()
            super.onAttach(context)
        }
    }

    abstract class Dialog : BaseDialog() {

        val mainVM by lazy { activityVM(MainVM::class) }

        val remainingVM by lazy { activityVM(TimeoutVM::class) }

        override fun onAttach(context: Context) {
            remainingVM.stopTimeout()
            super.onAttach(context)
        }
    }

}