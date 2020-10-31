package wee.digital.fpa.repository.payment

import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.repository.base.IBase
import wee.digital.fpa.repository.dto.*
import wee.digital.fpa.repository.model.ClientIDResp
import wee.digital.fpa.repository.network.Api

class PaymentRepository : IBase.Payment {

    private val mPaymentProvider = PaymentProvider()

    companion object {
        val ins: PaymentRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PaymentRepository()
        }
    }

    override fun getClientId(listener: Api.ClientListener<ClientIDResp>) {
        mPaymentProvider.getClientId(listener = listener)
    }

    override fun requestPayment(
            data: RequestPaymentDTOReq,
            listener: Api.ClientListener<RequestPaymentDTOResp>
    ) {
        mPaymentProvider.requestPayment(data = data, listener = listener)
    }

    override fun verifyFace(
            dataReq: VerifyFaceDTOReq,
            facePointData: FacePointData,
            listener: Api.ClientListener<FaceArg>
    ) {
        mPaymentProvider.verifyFace(dataReq = dataReq, facePointData = facePointData, listener = listener)
    }

    override fun verifyPINCode(
            dataReq: VerifyPINCodeDTOReq,
            listener: Api.ClientListener<PinArg>
    ) {
        mPaymentProvider.verifyPINCode(dataReq = dataReq, listener = listener)
    }

    // TODO: pin fragment arg to otp fragment
    override fun payment(dataReq: PaymentDTOReq, listener: Api.ClientListener<PaymentDTOResp>) {
        mPaymentProvider.payment(dataReq = dataReq, listener = listener)
    }

    override fun getBankAccList(
            dataReq: GetBankAccListDTOReq,
            listener: Api.ClientListener<GetBankAccListDTOResp>
    ) {
        mPaymentProvider.getBankAccList(dataReq, listener)
    }

    override fun updatePaymentStatus(dataReq: UpdatePaymentStatusDTOReq) {
        mPaymentProvider.updatePaymentStatus(dataReq = dataReq)
    }

    override fun updateCancelPayment(dataReq: UpdateCancelPaymentDTOReq) {
        mPaymentProvider.updateCancelPayment(dataReq = dataReq)
    }

}