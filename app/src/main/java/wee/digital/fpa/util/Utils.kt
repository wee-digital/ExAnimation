package wee.digital.fpa.util

import android.provider.Settings
import android.util.Log
import wee.digital.fpa.app.app
import wee.digital.fpa.camera.FacePointData
import java.text.DecimalFormat
import java.text.NumberFormat

object Utils{

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


}