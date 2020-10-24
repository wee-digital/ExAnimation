package wee.digital.fpa.ui.payment

import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.R
import wee.digital.library.extension.moneyFormat
import wee.digital.library.extension.nowFormat

class PaymentView(private val v: PaymentFragment) {

    fun onViewInit() {
        v.addClickListener(v.paymentViewAccept, v.paymentViewDeny)
    }

    fun bindPaymentInfo(obj: JsonObject) {
        v.paymentImageViewStation.setImageResource(R.drawable.drw_btn_primary)
        v.paymentTextViewStation.text = "GS25"
        v.paymentTextViewDate.text = nowFormat("dd tháng MM yyyy")
        v.paymentTextViewAmount.text = "123456789".moneyFormat()
    }


}