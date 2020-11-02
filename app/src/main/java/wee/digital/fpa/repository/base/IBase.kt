package wee.digital.fpa.repository.base

import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.repository.dto.*
import wee.digital.fpa.repository.model.ClientResponse
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.repository.network.Api

interface IBase {

    interface DeviceSystem {
        fun register(data: DeviceInfoStore, listener: Api.ClientListener<Any>)
        fun checkDeviceStatus(listener: Api.ClientListener<Int>)
        fun getToken(listener: Api.ClientListener<TokenResponse>)
    }

    interface Payment {
        fun getClientId(listener: Api.ClientListener<ClientResponse>)
        fun requestPayment(data: RequestPaymentDTOReq, listener: Api.ClientListener<RequestPaymentDTOResp>)
        fun verifyFace(dataReq: FaceRequest, facePointData: FacePointData, listener: Api.ClientListener<FaceResponse>)
        fun verifyPINCode(dataReq: PinRequest, listener: Api.ClientListener<PinResponse>)
        fun payment(dataReq: PaymentDTOReq, listener: Api.ClientListener<PaymentResponse>)
        fun getBankAccList(dataReq: GetBankAccListDTOReq, listener: Api.ClientListener<CardListResponse>)
        fun updatePaymentStatus(dataReq: UpdatePaymentStatusDTOReq)
        fun updateCancelPayment(dataReq: UpdateCancelPaymentDTOReq)
    }
}