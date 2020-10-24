package wee.digital.fpa.ui.payment.progress

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PaymentProgressViewModel: ViewModel() {
    var paymentProgressData: LiveData<Boolean>?= null
}