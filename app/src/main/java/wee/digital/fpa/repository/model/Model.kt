package wee.digital.fpa.repository.model

import android.os.Bundle
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import wee.digital.fpa.repository.dto.FaceResponse
import wee.digital.fpa.repository.dto.PaymentResponse
import wee.digital.fpa.repository.dto.PinResponse

/**
 * [wee.dev.faceposv2.repository.network.EncryptData]
 */
data class EncryptResult(val headers: HashMap<String, Any>, val body: ByteArray)

data class EncryptResultCollect(val headers: HashMap<String, Any>, val body: String)

data class EncryptResultCheckDevice(val headers: HashMap<String, Any>, val body: ByteArray, val privateKey: ByteArray)

data class ObjectDefault(var uid: String)

data class DataRegisterDevice(val name: String = "", val status: Int = 0)

/**
 * [wee.dev.faceposv2.ui.payment.PaymentViewModel.verifyFace]
 */
data class PaymentVerifyFace(val dataResp: FaceResponse, val bundle: Bundle)

/**
 * [wee.dev.faceposv2.ui.payment.PaymentViewModel.verifyPin]
 */
data class PaymentVerifyPin(val dataResp: PinResponse, val bundle: Bundle)

/**
 * [wee.dev.faceposv2.ui.payment.PaymentViewModel.payment]
 */
data class PaymentFacePay(val dataResp: PaymentResponse, val bundle: Bundle)

/**
 * [wee.dev.faceposv2.ui.payment.PaymentViewModel.getListBank]
 */
data class PaymentListBank(val bankAccList: ArrayList<BankAccInfo>, val bundle: Bundle)

/**
 * [wee.dev.faceposv2.utils.Utils]
 */
data class LogStreams(
        @SerializedName("streams")
        @Expose
        var streams: List<MyStream>? = null
)

class MyStream(
        var stream: HashMap<String, String>,
        var values: Array<Array<String>>
)

data class DataPermission(val permission: Boolean, val bundle: Bundle?)

data class DataContentFail(val title: String = "", val mess: String = "")
