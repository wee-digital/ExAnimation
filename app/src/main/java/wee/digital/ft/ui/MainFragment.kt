package wee.digital.ft.ui

import wee.digital.ft.ui.base.BaseFragment
import wee.digital.ft.ui.base.activityVM
import wee.digital.ft.ui.vm.SharedVM

abstract class MainFragment : BaseFragment() {

    val sharedVM by lazy { activityVM(SharedVM::class) }

}