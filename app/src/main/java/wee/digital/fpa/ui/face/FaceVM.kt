package wee.digital.fpa.ui.face

import androidx.lifecycle.ViewModel
import wee.digital.fpa.app.toast
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.repository.dto.VerifyFaceDTOReq
import wee.digital.fpa.repository.dto.VerifyFaceDTOResp
import wee.digital.fpa.repository.network.Api
import wee.digital.fpa.repository.network.CollectionData
import wee.digital.fpa.repository.payment.PaymentRepository

class FaceVM : ViewModel(){

    fun verifyFace(req : VerifyFaceDTOReq, dataFace : FacePointData, dataColl : DataCollect){
        PaymentRepository.ins.verifyFace(req, dataFace, object : Api.ClientListener<VerifyFaceDTOResp>{
            override fun onSuccess(data: VerifyFaceDTOResp) {
                toast("verify face success")
                CollectionData.instance.encryptCollData(dataColl)
            }

            override fun onFailed(code: Int, message: String) {
                super.onFailed(code, message)
                toast("show UI fail")
            }

        })
    }

}