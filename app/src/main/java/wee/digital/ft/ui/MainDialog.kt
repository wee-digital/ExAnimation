package wee.digital.ft.ui

import wee.digital.ft.ui.base.BaseDialog
import wee.digital.ft.ui.base.activityVM
import wee.digital.ft.ui.vm.SharedVM

abstract class MainDialog : BaseDialog() {

    protected val sharedVM by lazy { activityVM(SharedVM::class) }

}