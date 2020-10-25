package wee.digital.fpa.ui

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    val vm by lazy { viewModel(MainVM::class) }

    override fun layoutResource(): Int {
        return R.layout.main
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        Main.direction.observe {
            navigate(it) {
                setLaunchSingleTop(true)
                setInclusive(false)
            }
        }
        Main.messageArg.observe {
            navigate(MainDirections.actionGlobalMessageFragment())
        }
        Main.confirmArg.observe {
            navigate(MainDirections.actionGlobalConfirmFragment())
        }
    }


}