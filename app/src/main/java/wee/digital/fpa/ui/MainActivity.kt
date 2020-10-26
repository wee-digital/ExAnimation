package wee.digital.fpa.ui

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseActivity
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.confirm.ConfirmVM
import wee.digital.fpa.ui.message.MessageVM

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
    }


}