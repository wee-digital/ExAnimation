package wee.digital.fpa.repository.base

import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.repository.dto.VerifyPINCodeDTOReq
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.dto.*
import wee.digital.fpa.repository.model.ClientIDResp
import wee.digital.fpa.repository.model.DeviceInfoStore

interface IBase {

    interface DeviceSystem{
        fun register(data: DeviceInfoStore, listener: Api.ClientListener<Any>)
        fun checkDeviceStatus(listener: Api.ClientListener<Int>)
        fun getToken(listener: Api.ClientListener<GetTokenDTOResp>)
    }

    interface Payment{
        fun getClientId(listener: Api.ClientListener<ClientIDResp>)
        fun requestPayment(data: RequestPaymentDTOReq, listener: Api.ClientListener<RequestPaymentDTOResp>)
        fun verifyFace(dataReq: VerifyFaceDTOReq, facePointData: FacePointData, listener: Api.ClientListener<VerifyFaceDTOResp>)
        fun verifyPINCode(dataReq: VerifyPINCodeDTOReq, listener: Api.ClientListener<VerifyPINCodeDTOResp>)
        fun payment(dataReq: PaymentDTOReq, listener: Api.ClientListener<PaymentDTOResp>)
        fun getBankAccList(dataReq: GetBankAccListDTOReq, listener: Api.ClientListener<GetBankAccListDTOResp>)
        fun updatePaymentStatus(dataReq: UpdatePaymentStatusDTOReq)
        fun updateCancelPayment(dataReq : UpdateCancelPaymentDTOReq)
    }
}