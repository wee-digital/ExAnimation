package wee.digital.fpa.ui

import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.vm.SharedVM

abstract class MainDialog : BaseDialog() {

    protected val sharedVM by lazy { activityVM(SharedVM::class) }

}