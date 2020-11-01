package wee.digital.fpa.util

import android.content.res.Resources
import android.provider.Settings
import android.util.Log
import wee.digital.fpa.app.app
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.repository.model.DataContentFail
import wee.digital.fpa.repository.utils.ErrCode
import java.io.File

object Utils {

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
        Log.d("dataFaceUtils", "$dataFace")
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
    fun deleteCache() {
        try {
            val dir = app.cacheDir
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
    fun getContentFailFromCode(code: Int,block: (String,String)->Unit) {
        when (code) {
            ErrCode.BANK_UNKNOWN_ERROR -> {
                block("Thanh toán không thành công",
                        "Tài khoản ngân hàng liên kết hiện tại không thể thực hiện thanh toán. Bạn Vui lòng thử lại")
            }

            ErrCode.USER_BANK_ACC_NOT_FOUND -> {
                block("Thanh toán không thành công",
                        "Không tìm thấy tài khoản ngân hàng đã liên kết")
            }

            ErrCode.ACC_FACEPAY_LOCKED, ErrCode.ACC_LOCKED -> {
                block("Thanh toán không thành công",
                        "Tài khoản Facepay của bạn hiện đang tạm khóa chức năng thanh toán.")
            }

            ErrCode.NOT_LINKED_BANK_ACC, ErrCode.BANK_ACC_NOT_FOUND -> {
                block("Tài khoản chưa liên kết ngân hàng",
                        "Giao dịch không thể thực hiện do tài khoản của bạn chưa liên kết ngân hàng. Bạn vui lòng liên kết tài khoản trước khi thanh toán")
            }

            ErrCode.INSUFFICIENT_ACC_BALANCE -> {
                block("Số dư không đủ để thực hiện thanh toán",
                        "Tài khoản liên kết hiện tại không đủ số dư để thực hiện giao dịch")

            }

            ErrCode.FACE_NOT_FOUND -> {
                block("Tài khoản không tồn tại",
                        "Bạn vui lòng đăng ký tài khoản Facepay trước khi thực hiện thanh toán")
            }

            ErrCode.MAX_PIN_CODE_VERIFICATION, ErrCode.WRONG_PIN_CODE -> {
                block("Quá số lần nhập mã PIN",
                        "Bạn đã nhập sai mã PIN quá số lần cho phép, giao dịch không thể thực hiện.")
            }

            ErrCode.USER_IS_OWNER -> {
                block("Thanh toán không thành công","Chủ shop không thể thanh toán, bạn vui lòng thử lại")
            }

            ErrCode.TIMEOUT_ERROR -> {
                block("Hết thời gian thanh toán","Giao dịch của bạn đã quá thời gian thanh toán. Bạn vui lòng thực hiện lại giao dịch.")
            }

            ErrCode.TRANS_EXCEED_LIMIT -> {
                block("Quá hạn mức giao dịch","Giao dịch không thể thực hiện do số tiền yêu cầu thanh toán vượt quá hạn mức giao dịch của tài khoản.")

            }

            ErrCode.DEVICE_DELETE -> {
                block("Thiết bị mất kết nối với hệ thống quản trị","Bạn vui lòng kết nối lại thiết bị")

            }

            ErrCode.MAX_FACE_VERIFICATION -> {
                block("Nhận diện khuôn mặt không thành công","Bạn đã quá số lần nhận diện khuôn mặt cho phép, giao dịch không thể thực hiện.")

            }

            ErrCode.FACE_PAY_ERROR -> {
                block("Thanh toán không thành công","Phát hiện lỗi, Vui lòng thử lại")

            }

            ErrCode.BILL_PAID -> {
                block("Thanh toán không thành công","Giao dịch của bạn đã được thanh toán trước đó")

            }

            else -> {
                block("Thanh toán không thành công","Phát hiện lỗi, Vui lòng thử lại")
            }
        }
    }

}