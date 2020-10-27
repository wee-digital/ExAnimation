package wee.digital.fpa.util

import android.content.Context
import android.content.res.Resources
import android.provider.Settings
import android.util.Log
import wee.digital.fpa.app.app
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.repository.model.DataContentFail
import wee.digital.fpa.repository.utils.ErrCode
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat

object Utils {

    /**
     * format string [formatMoney], [formatSpace], [formatDataFaceHeader]
     */
    fun formatMoney(money: String): String {
        if (money.isEmpty()) return ""
        val formatter: NumberFormat = DecimalFormat("#,###")
        val cash = formatter.format(money.toDouble()).replace(",", ".")
        return cash
    }

    fun formatSpace(str: String): String {
        return str.replace(" ", "")
    }

    fun formatDataFaceHeader(data: FacePointData?): String? {
        data ?: return null
        val rect = data.faceRect
        val eyeLeft = data.LeftEye
        val eyeRight = data.RightEye
        val mouthLeft = data.Leftmouth
        val mouthRight = data.Rightmouth
        val nose = data.Nose
        val dataFace =
                "${rect.left}a${rect.top}a${rect.right}a${rect.bottom}a${eyeLeft.x}a${eyeLeft.y}a${eyeRight.x}a${eyeRight.y}a${mouthLeft.x}a${mouthLeft.y}a${mouthRight.x}a${mouthRight.y}a${nose.x}a${nose.y}"
        Log.e("dataFaceUtils", "$dataFace")
        return "$dataFace"
    }


    /**
     * get data System [getIdDevice], [getMemory]
     */
    fun getIdDevice(): String {
        val id = try {
            Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: java.lang.Exception) {
            ""
        }
        val time = System.currentTimeMillis().toString()
        val timeSub = if (time.length > 4) time.substring(time.length - 4, time.length) else time
        return "$id$timeSub"
    }

    /**
     * delete cache [deleteCache]
     */
    fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteDir(dir)
        } catch (e: java.lang.Exception) {
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    /**
     * set content Fail
     */
    fun getContentFailFromCode(code : Int) : DataContentFail{
        var title = ""
        var mess = ""
        when(code) {
            ErrCode.BANK_UNKNOWN_ERROR -> {
                title = "Thanh toán không thành công"
                mess = "Tài khoản ngân hàng liên kết hiện tại không thể thực hiện thanh toán. Bạn Vui lòng thử lại"
            }

            ErrCode.USER_BANK_ACC_NOT_FOUND -> {
                title = "Thanh toán không thành công"
                mess = "Không tìm thấy tài khoản ngân hàng đã liên kết"
            }

            ErrCode.ACC_FACEPAY_LOCKED, ErrCode.ACC_LOCKED -> {
                title = "Không thể thực hiện giao dịch"
                mess = "Tài khoản Facepay của bạn hiện đang tạm khóa chức năng thanh toán."
            }

            ErrCode.NOT_LINKED_BANK_ACC, ErrCode.BANK_ACC_NOT_FOUND -> {
                title = "Tài khoản chưa liên kết ngân hàng"
                mess = "Giao dịch không thể thực hiện do tài khoản của bạn chưa liên kết ngân hàng. Bạn vui lòng liên kết tài khoản trước khi thanh toán"
            }

            ErrCode.INSUFFICIENT_ACC_BALANCE -> {
                title = "Số dư không đủ để thực hiện thanh toán"
                mess = "Tài khoản liên kết hiện tại không đủ số dư để thực hiện giao dịch"
            }

            ErrCode.FACE_NOT_FOUND -> {
                title = "Tài khoản không tồn tại"
                mess = "Bạn vui lòng đăng ký tài khoản Facepay trước khi thực hiện thanh toán"
            }

            ErrCode.MAX_PIN_CODE_VERIFICATION, ErrCode.WRONG_PIN_CODE -> {
                title = "Quá số lần nhập mã PIN"
                mess = "Bạn đã nhập sai mã PIN quá số lần cho phép, giao dịch không thể thực hiện."
            }

            ErrCode.USER_IS_OWNER -> {
                title = "Thanh toán không thành công"
                mess = "Chủ shop không thể thanh toán, bạn vui lòng thử lại"
            }

            ErrCode.TIMEOUT_ERROR -> {
                title = "Hết thời gian thanh toán"
                mess = "Giao dịch của bạn đã quá thời gian thanh toán. Bạn vui lòng thực hiện lại giao dịch."
            }

            ErrCode.TRANS_EXCEED_LIMIT -> {
                title = "Quá hạn mức giao dịch"
                mess = "Giao dịch không thể thực hiện do số tiền yêu cầu thanh toán vượt quá hạn mức giao dịch của tài khoản."
            }

            ErrCode.DEVICE_DELETE -> {
                title = "Thiết bị mất kết nối với hệ thống quản trị"
                mess = "Bạn vui lòng kết nối lại thiết bị"
            }

            ErrCode.MAX_FACE_VERIFICATION -> {
                title = "Nhận diện khuôn mặt không thành công"
                mess = "Bạn đã quá số lần nhận diện khuôn mặt cho phép, giao dịch không thể thực hiện."
            }

            ErrCode.FACE_PAY_ERROR -> {
                title = "Thanh toán không thành công"
                mess = "Phát hiện lỗi, Vui lòng thử lại"
            }

            ErrCode.BILL_PAID -> {
                title = "Thanh toán không thành công"
                mess = "Giao dịch của bạn đã được thanh toán trước đó"
            }

            else -> {
                title = "Thanh toán không thành công"
                mess = "Phát hiện lỗi, Vui lòng thử lại"
            }
        }
        return DataContentFail(title, mess)
    }

}