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


}