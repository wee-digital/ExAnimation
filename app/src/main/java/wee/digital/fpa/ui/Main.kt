package wee.digital.fpa.ui

import androidx.navigation.NavDirections
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.progress.ProgressVM
import wee.digital.fpa.ui.vm.TimeoutVM

class Main {

    companion object {
        val rootDirection by lazy {
            EventLiveData<NavDirections>()
        }
    }

    abstract class Fragment : BaseFragment() {

        val timeoutVM by lazy { activityVM(TimeoutVM::class) }
    }

    abstract class Dialog : BaseDialog() {

        val progressVM by lazy { viewModel(ProgressVM::class) }

        val mainVM by lazy { activityVM(MainVM::class) }

        val timeoutVM by lazy { activityVM(TimeoutVM::class) }
    }

}