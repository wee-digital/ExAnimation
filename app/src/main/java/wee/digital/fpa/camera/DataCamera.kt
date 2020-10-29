package wee.digital.fpa.camera

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import androidx.room.Ignore
import wee.digital.fpa.repository.model.FaceInfo
import kotlin.math.roundToInt

data class DataCollect(
        var unit: Float = 0f,
        @Ignore
        var depthData: ByteArray? = null,
        @Ignore
        var colorData: ByteArray? = null,
        val width: Int = RealSenseControl.COLOR_WIDTH,
        val height: Int = RealSenseControl.COLOR_HEIGHT,
        var device: RealSenseControl.DeviceConfig = RealSenseControl.DeviceConfig(),
        var userInfo: FaceInfo? = null,
        var pointData: String = "",
        var dataFacePoint: DataGetFacePoint? = null,
        var isRepaired: Boolean = false,
        var frameColorString: String = "",
        var frameDepthString: String = ""
)

data class FacePointData(
        var faceRect: Rect,
        var RightEye: Point,
        var LeftEye: Point,
        var Nose: Point,
        var Rightmouth: Point,
        var Leftmouth: Point
)

data class DataGetFacePoint(
        val dataFace: FacePointData?,
        val face: ByteArray?
)

fun Rect.cropPortrait(bitmap: Bitmap): Bitmap? {

    val plusH = height() * 0.5
    val minusWH = (height() - width()) / 2

    val plusW = (width() + minusWH) * 0.5
    val height = height() + plusH.roundToInt()
    val width = width() + plusW.roundToInt()

    var top = top - (plusH / 2).roundToInt()
    var left = left - (plusW / 2).roundToInt()
    if (top < 0) top = 0
    if (left < 0) left = 0

    val newRect = Rect(left, top, left + width, top + height)
    val rectCrop = newRect.getRectCrop(bitmap)
    val copiedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    return try {
        val crop = Bitmap.createBitmap(
                copiedBitmap,
                rectCrop.left,
                rectCrop.top,
                rectCrop.width(),
                rectCrop.height()
        )
        crop
    } catch (t: Throwable) {
        null
    }
}

fun Rect.getRectCrop(bitmap: Bitmap): Rect {
    val top = if (top < 0) 0 else top
    val left = if (left < 0) 0 else left
    val right = if (right > bitmap.width) bitmap.width else right
    val bottom = if (bottom > bitmap.height) bitmap.height else bottom
    return Rect(left, top, right, bottom)
}