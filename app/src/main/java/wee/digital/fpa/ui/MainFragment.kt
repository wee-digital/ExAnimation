package wee.digital.fpa.ui

import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.vm.SharedVM

abstract class MainFragment : BaseFragment() {

    protected val sharedVM by lazy { activityVM(SharedVM::class) }

}