package wee.digital.fpa.ui.card

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.GetBankAccListDTOReq
import wee.digital.fpa.repository.dto.GetBankAccListDTOResp
import wee.digital.fpa.repository.dto.PaymentDTOReq
import wee.digital.fpa.repository.dto.PaymentResponse
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.payment.PaymentRepository
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import wee.digital.fpa.ui.payment.PaymentArg
import wee.digital.fpa.ui.pin.PinArg
import wee.digital.library.extension.listString
import wee.digital.library.extension.str
import wee.digital.library.extension.toObject

class CardVM : BaseViewModel() {

    val arg = MutableLiveData<String?>()

    val cardList = MutableLiveData<List<CardItem>?>()

    val otpForm = EventLiveData<String>()

    override fun onStart() {
    }

    fun fetchCardList(pinArg: PinArg?) {
        val body = GetBankAccListDTOReq(
                userID = pinArg?.userId ?: throw Event.pinDataError
        )
        PaymentRepository.ins.getBankAccList(body, object : Api.ClientListener<GetBankAccListDTOResp> {
            override fun onSuccess(response: GetBankAccListDTOResp) {
                onCardListResponse(response)
            }

            override fun onFailed(code: Int, message: String) {
                cardList.postValue(null)
            }

        })
    }

    private fun onCardListResponse(data: GetBankAccListDTOResp) {
        val banks = Shared.bankJson.value ?: throw Event.bankDataError
        val accBanks = data.basicBankAccList
        val cardItems = mutableListOf<CardItem>()
        for (i in 0 until banks.size()) {
            for (j in 0..accBanks.lastIndex) {
                val obj = banks.get(i).toObject() ?: continue
                val accBanks = accBanks[j]
                if (obj.str("BankCode") != accBanks.BankCode) continue
                val item = CardItem(
                        id = accBanks.id,
                        bankCode = obj.str("BankCode") ?: continue,
                        name = obj.str("BankName") ?: continue,
                        shortName = obj.str("BankShortName") ?: continue,
                        colors = obj.listString("BackgroundColors") ?: listOf("#F58220", "#FF9C48"),
                )
                cardItems.add(item)
                break
            }
        }
        cardList.postValue(cardItems)
    }

    fun postPayRequest(bankCode: String, paymentArg: PaymentArg?) {
        paymentArg ?: throw Event.paymentArgError
        val body = PaymentDTOReq(
                paymentID = paymentArg.paymentId,
                clientIP = paymentArg.clientIp,
                accountID = bankCode
        )
        PaymentRepository.ins.payment(body, object : Api.ClientListener<PaymentResponse> {
            override fun onSuccess(response: PaymentResponse) {
                when {
                    response.code == 0 -> {
                        eventLiveData.postValue(CardEvent.PAY_SUCCESS)
                    }
                    response.haveOTP && !response.formOtp.isNullOrEmpty() -> {
                        otpForm.postValue(response.formOtp)
                    }
                    else -> {
                        eventLiveData.postValue(CardEvent.PAY_FAILED)
                    }
                }
            }
        })
    }

}