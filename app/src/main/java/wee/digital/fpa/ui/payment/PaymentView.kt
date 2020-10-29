package wee.digital.fpa.ui.payment

import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.repository.utils.SystemUrl
import wee.digital.fpa.ui.arg.PaymentArg
import wee.digital.fpa.util.observerCameraStream
import wee.digital.library.extension.load
import wee.digital.library.extension.moneyFormat
import wee.digital.library.extension.nowFormat

class PaymentView(private val v: PaymentFragment) {

    fun onViewInit() {
        v.addClickListener(v.paymentViewAccept, v.paymentViewDeny)
    }

    fun onPaymentDataChanged(it: PaymentArg?) {
        v.paymentTextViewDate.text = "${nowFormat("dd")} tháng ${nowFormat("MM yyyy")}"
        v.paymentTextViewAmount.text = it?.amount.moneyFormat()
    }

    fun onDeviceInfoChanged(it: DeviceInfo?) {
        it?.apply {
            v.paymentImageViewStation.load("%s%s".format(SystemUrl.LOGO_SHOP, shopID))
            v.paymentTextViewStation.text = fullName
            v.paymentTextViewStation2.text = fullName
        }
    }
}