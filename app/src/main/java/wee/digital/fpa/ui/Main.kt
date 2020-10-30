package wee.digital.fpa.ui

import androidx.navigation.NavDirections
import wee.digital.fpa.MainDirections
import wee.digital.fpa.ui.base.*
import wee.digital.fpa.ui.progress.ProgressVM
import wee.digital.fpa.ui.vm.TimeoutVM

class Main {

    interface MainFragmentInt : BaseView {

        fun navigateAdvFragment() {
            navigate(MainDirections.actionGlobalAdvFragment()) {
                setNoneAnim()
                setLaunchSingleTop()
            }
        }
        fun navigateSplashFragment() {
            navigate(MainDirections.actionGlobalSplashFragment()) {
                setNoneAnim()
                setLaunchSingleTop()
            }
        }
    }

    abstract class Fragment : BaseFragment(), MainFragmentInt {

        val timeoutVM by lazy { activityVM(TimeoutVM::class) }
    }

    abstract class Dialog : BaseDialog(), MainFragmentInt {

        val progressVM by lazy { viewModel(ProgressVM::class) }

        val mainVM by lazy { activityVM(MainVM::class) }

        val timeoutVM by lazy { activityVM(TimeoutVM::class) }


    }

    companion object {
        val mainDirection by lazy {
            EventLiveData<NavDirections>()
        }
    }


}