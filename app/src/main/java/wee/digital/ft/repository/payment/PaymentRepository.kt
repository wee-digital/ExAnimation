package wee.digital.ft.repository.payment

import wee.digital.ft.camera.FacePointData
import wee.digital.ft.repository.base.IBase
import wee.digital.ft.repository.dto.*
import wee.digital.ft.repository.model.ClientResponse
import wee.digital.ft.repository.network.Api

class PaymentRepository : IBase.Payment {

    private val mPaymentProvider = PaymentProvider()

    companion object {
        val ins: PaymentRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PaymentRepository()
        }
    }

    override fun getClientId(listener: Api.ClientListener<ClientResponse>) {
        mPaymentProvider.getClientId(listener = listener)
    }

    override fun requestPayment(
            data: RequestPaymentDTOReq,
            listener: Api.ClientListener<RequestPaymentDTOResp>
    ) {
        mPaymentProvider.requestPayment(data = data, listener = listener)
    }

    override fun verifyFace(
            dataReq: FaceRequest,
            facePointData: FacePointData,
            listener: Api.ClientListener<FaceResponse>
    ) {
        mPaymentProvider.verifyFace(dataReq = dataReq, facePointData = facePointData, listener = listener)
    }

    override fun verifyPINCode(
            dataReq: PinRequest,
            listener: Api.ClientListener<PinResponse>
    ) {
        mPaymentProvider.verifyPINCode(dataReq = dataReq, listener = listener)
    }

    override fun payment(dataReq: PaymentDTOReq, listener: Api.ClientListener<PaymentResponse>) {
        mPaymentProvider.payment(dataReq = dataReq, listener = listener)
    }

    override fun getBankAccList(
            dataReq: GetBankAccListDTOReq,
            listener: Api.ClientListener<CardListResponse>
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