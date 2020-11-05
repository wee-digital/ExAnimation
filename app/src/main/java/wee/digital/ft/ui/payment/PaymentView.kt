package wee.digital.ft.ui.payment

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.payment.view.*
import wee.digital.ft.repository.model.DeviceInfo
import wee.digital.ft.repository.utils.SystemUrl
import wee.digital.library.extension.load
import wee.digital.library.extension.moneyFormat
import wee.digital.library.extension.nowFormat

class PaymentView : ConstraintLayout {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    fun onPaymentDataChanged(it: PaymentArg?) {
        it ?: return
        paymentTextViewDate.text = "${nowFormat("dd")} tháng ${nowFormat("MM yyyy")}"
        paymentTextViewAmount.text = it.amount.moneyFormat()
    }

    fun onDeviceInfoChanged(it: DeviceInfo?) {
        it?.apply {
            paymentImageViewStation.load("%s%s".format(SystemUrl.LOGO_SHOP, shopID))
            paymentTextViewStation.text = shopName
            paymentTextViewStation2.text = shopName
        }
    }
}