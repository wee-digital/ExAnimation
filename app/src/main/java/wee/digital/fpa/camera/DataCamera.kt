package wee.digital.fpa.camera

import android.graphics.Point
import android.graphics.Rect
import androidx.room.Ignore
import wee.digital.fpa.repository.model.FaceInfo

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
        var frameColorString : String = "",
        var frameDepthString : String = ""
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
