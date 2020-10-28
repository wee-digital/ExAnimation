package wee.digital.fpa.ui.base

import androidx.lifecycle.ViewModel
import wee.digital.fpa.repository.base.BaseData
import wee.digital.log.Logger

abstract class BaseViewModel : ViewModel() {

    val log by lazy { Logger(this::class) }

    val deviceInfo get() = BaseData.ins.getDeviceInfoPref()
}