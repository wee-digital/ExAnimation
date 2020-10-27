package wee.digital.fpa.ui.base

import androidx.lifecycle.ViewModel
import wee.digital.log.Logger

abstract class BaseViewModel : ViewModel() {

    val log by lazy { Logger(this::class) }
}