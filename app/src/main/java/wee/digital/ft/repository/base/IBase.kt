package wee.digital.ft.repository.base

import wee.digital.ft.camera.FacePointData
import wee.digital.ft.repository.dto.*
import wee.digital.ft.repository.model.ClientResponse
import wee.digital.ft.repository.model.DeviceInfoStore
import wee.digital.ft.repository.network.Api

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