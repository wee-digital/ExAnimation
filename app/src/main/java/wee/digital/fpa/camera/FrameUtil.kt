package wee.digital.fpa.camera

import android.graphics.*
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.intel.realsense.librealsense.Frame
import crypto.Crypto
import wee.digital.fpa.repository.utils.Key
import java.util.*
import java.util.regex.Pattern
import kotlin.math.*

object FrameUtil {

    const val TAG = "FrameUtil"

    /**
     * rotation bitmap [rotateBitmap]
     */
    fun rotateBitmap(bitmap: Bitmap, degree: Float?): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree!!)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * get rectDepthFace [getRectDepthFace], [getRectDepthFaceFullHD]
     */
    fun getRectDepthFace(faceRect: Rect): Rect {
        val leftCorner = Detection.leftCorner
        val topCorner = Detection.topCorner
        val scale = Detection.scale
        val x = faceRect.exactCenterX() * scale + leftCorner / scale
        val y = faceRect.exactCenterY() * scale + topCorner
        val width = faceRect.width() * scale
        val height = faceRect.height() * scale
        val left = x - width / 2
        val top = y - height / 2
        val right = x + width / 2
        val bottom = y + height / 2
        return Rect(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
    }

    fun getRectDepthFaceFullHD(faceRect: Rect): Rect {
        val leftCorner = 1
        val topCorner = 35
        val scale = 0.36
        val x = faceRect.exactCenterX() * scale + leftCorner / scale
        val y = faceRect.exactCenterY() * scale + topCorner
        val width = faceRect.width() * scale
        val height = faceRect.height() * scale
        val left = x - width / 2
        val top = y - height / 2
        val right = x + width / 2
        val bottom = y + height / 2
        return Rect(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
    }

    /**
     * crop bitmap width rect [cropBitmapWithFace], [cropBitmapWithRect]
     */
    fun cropBitmapWithFace(bitmap: Bitmap?, box: Box): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val rect = box.transform2Rect()
        var top = rect.top
        if (top < 0) {
            top = 0
        }
        //---
        var left = rect.left
        if (left < 0) {
            left = 0
        }

        val height = rect.height()
        //---
        val width = rect.width() + (height / 10)
        //---
        var x = left
        var y = top
        if (x < 0) x = 0
        if (y < 0) y = 0
        return try {
            if (y + height > bitmap.height || x + width > bitmap.width) {
                val cropBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                bitmap.recycle()
                cropBitmap
            } else {
                val cropBitmap = Bitmap.createBitmap(bitmap, x, y, width, height)
                bitmap.recycle()
                cropBitmap
            }

        } catch (ex: Exception) {
            Log.e("getFaceResult", ex.message.toString())
            bitmap.recycle()
            null
        }
    }

    fun cropFace(bitmap: Bitmap?, box: Box): Bitmap? {
        bitmap ?: return null
        val rect = box.transform2Rect()
        val height = rect.height()
        val width = rect.width()
        val top = if (rect.top < 0) 0 else rect.top
        val left = if (rect.left < 0) 0 else rect.left
        val rectCrop = getRectCrop(bitmap, Rect(left, top, left + width, top + height))
        val copiedBitmap = bitmap.copy(Bitmap.Config.RGB_565, true)
        return try {
            Bitmap.createBitmap(
                    copiedBitmap,
                    rectCrop.left,
                    rectCrop.top,
                    rectCrop.width(),
                    rectCrop.height()
            )
        } catch (ex: Exception) {
            copiedBitmap
        }
    }

    private fun getRectCrop(bitmap: Bitmap, rect: Rect): Rect {
        val top = if (rect.top < 0) 0 else rect.top
        val left = if (rect.left < 0) 0 else rect.left
        val right = if (rect.right > bitmap.width) bitmap.width else rect.right
        val bottom = if (rect.bottom > bitmap.height) bitmap.height else rect.bottom
        return Rect(left, top, right, bottom)
    }

    fun cropBitmapWithRect(bitmap: Bitmap?, rect: Rect): Bitmap? {
        bitmap ?: return null
        var top = rect.top
        if (top < 0) {
            top = 0
        }
        //---
        var left = rect.left
        if (left < 0) {
            left = 0
        }

        val height = rect.height()
        //---
        val width = rect.width()
        //---
        var x = left
        var y = top
        if (x < 0) x = 0
        if (y < 0) y = 0
        val rectCrop = Rect(left, top, x + width, y + height)
        return try {
            val cropBitmap = Bitmap.createBitmap(
                    bitmap,
                    rectCrop.left, rectCrop.top, rectCrop.width(), rectCrop.height()
            )
            bitmap.recycle()
            cropBitmap
        } catch (ex: Exception) {
            Log.e("getFaceResult", ex.message.toString())
            bitmap.recycle()
            null
        }
    }

    fun cropFaceWithPadding(bitmap: Bitmap?, rect: Rect): Bitmap? {
        bitmap ?: return null
        val extraH = 0.5f
        val extraW = 0.5f
        val plusH = rect.height() * extraH
        val plusW = rect.width() * extraW
        val height = rect.height() + plusH.roundToInt()
        val width = rect.width() + plusW.roundToInt()
        var top = rect.top - (plusH / 2).roundToInt()
        var left = rect.left - (plusW / 2).roundToInt()
        val copiedBitmap = bitmap.copy(Bitmap.Config.RGB_565, true)
        return try {
            Bitmap.createBitmap(
                    copiedBitmap,
                    left,
                    top,
                    width,
                    height
            )
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * get dataPoint in face
     */
    fun getDataFaceAndFace(bitmap: Bitmap?, box: Box): DataGetFacePoint {
        bitmap ?: return DataGetFacePoint(null, null)

        val rect = box.transform2Rect()

        val extraH = 0.5f
        val extraW = 0.5f

        val plusH = rect.height() * extraH
        val plusW = rect.width() * extraW

        val height = rect.height() + plusH.roundToInt()
        val width = rect.width() + plusW.roundToInt()
        val top = rect.top - (plusH / 2).roundToInt()
        val left = rect.left - (plusW / 2).roundToInt()

        val copiedBitmap = bitmap.copy(Bitmap.Config.RGB_565, true)
        try {
            val bmFace = Bitmap.createBitmap(
                    copiedBitmap,
                    left,
                    top,
                    width,
                    height
            )
            val facePointData =
                    getDataFace(
                            box,
                            (rect.width() * 0.5f).toInt(),
                            (rect.height() * 0.5f).toInt()
                    )
            val byteFace = BitmapUtils.bitmapToByteArray(bmFace)
            bitmap.recycle()
            bmFace?.recycle()
            return DataGetFacePoint(facePointData, byteFace)
        } catch (ex: Exception) {
            val facePointData = getDataPoint(box)
            val byteFace = BitmapUtils.bitmapToByteArray(bitmap)
            bitmap.recycle()
            return DataGetFacePoint(facePointData, byteFace)
        }
    }

    fun getDataPoint(face: Box): FacePointData {
        val listNewPoint = arrayListOf<Point>()
        for (point in face.landmark) {
            val newP = convertPoint(point, face.transform2Rect())
            listNewPoint.add(newP)
        }

        return FacePointData(
                face.transform2Rect(),
                listNewPoint[1],
                listNewPoint[0],
                listNewPoint[2],
                listNewPoint[4],
                listNewPoint[3]
        )
    }

    fun getDataFace(face: Box, plusW: Int, plusH: Int): FacePointData {
        val faceRect = face.transform2Rect()
        val newRect =
                Rect(
                        0 + plusW / 2,
                        0 + plusH / 2,
                        faceRect.width() + plusW / 2,
                        faceRect.height() + plusH / 2
                )
        val listNewPoint = arrayListOf<Point>()
        for (point in face.landmark) {
            val newP = convertPoint(point, face.transform2Rect(), plusW, plusH)
            listNewPoint.add(newP)
        }

        return FacePointData(
                newRect,
                listNewPoint[1],
                listNewPoint[0],
                listNewPoint[2],
                listNewPoint[4],
                listNewPoint[3]
        )
    }

    private fun convertPoint(oldPoint: Point?, faceRect: Rect): Point {
        oldPoint ?: return Point(0, 0)
        val old_X = oldPoint.x
        val old_Y = oldPoint.y
        return Point(old_X, old_Y)
    }

    private fun convertPoint(oldPoint: Point?, faceRect: Rect, plusW: Int, plusH: Int): Point {
        oldPoint ?: return Point(0, 0)
        val old_X = oldPoint.x
        val old_Y = oldPoint.y
        val new_X = old_X - faceRect.left + plusW / 2
        val new_Y = old_Y - faceRect.top + plusH / 2
        return Point(new_X, new_Y)
    }

    /**
     * flipBitmap
     */
    private fun flipBitmap(src: Bitmap): Bitmap {
        val m = Matrix()
        m.preScale((-1).toFloat(), 1f)
        val dst: Bitmap = Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, false)
        dst.density = DisplayMetrics.DENSITY_DEFAULT
        src.recycle()
        return dst
    }

    /**
     * convert frame camera realsense to bitmap [getBitmapFromFrame]
     */
    private fun rgb8ToArgb(rgb8: ByteArray, width: Int, height: Int): IntArray? {
        try {
            val frameSize = width * height
            val rgb = IntArray(frameSize)
            var index = 0
            for (j in 0 until height) {
                for (i in 0 until width) {
                    val B = rgb8[3 * index].toInt()
                    val G = rgb8[3 * index + 1].toInt()
                    val R = rgb8[3 * index + 2].toInt()
                    rgb[index] = (R and 0xff) or (G and 0xff shl 8) or (B and 0xff shl 16)
                    index++
                }
            }
            return rgb
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return null
        }

    }

    fun getBitmapFromFrame(frame: Frame?, width: Int, height: Int): Bitmap? {
        return try {
            frame ?: return null
            val data = ByteArray(frame.dataSize)
            frame.getData(data)
            val argb = rgb8ToArgb(data, width, height)
                    ?: return null
            val bmp = Bitmap.createBitmap(argb, width, height, Bitmap.Config.RGB_565)
            return flipBitmap(bmp)
        } catch (e: OutOfMemoryError) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun getBitmapFromFrame(data: ByteArray?, width: Int, height: Int): Bitmap? {
        data ?: return null
        return try {
            val argb = rgb8ToArgb(data, width, height)
                    ?: return null
            val bmp = Bitmap.createBitmap(argb, width, height, Bitmap.Config.RGB_565)
            return flipBitmap(bmp)
        } catch (e: OutOfMemoryError) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun getBitmapFromByte(data: ByteArray, width: Int, height: Int): Bitmap? {
        try {
            val argb = rgb8ToArgb(data, width, height)
                    ?: return null
            val bmp = Bitmap.createBitmap(argb, width, height, Bitmap.Config.RGB_565)
            return flipBitmap(bmp)
        } catch (e: java.lang.Exception) {
            return null
        }
    }

    fun getFrameData(frame: Frame?): ByteArray? {
        frame ?: return null
        val data = ByteArray(frame.dataSize)
        frame.getData(data)
        return data
    }

    fun repairCollectData(collData: DataCollect): DataCollect? {
        return if (!collData.isRepaired) {
            val colorData = collData.colorData
            if (colorData != null) {

                val bitmap = getBitmapFromFrame(colorData, RealSenseControl.COLOR_WIDTH, RealSenseControl.COLOR_HEIGHT)
                val byteArr = BitmapUtils.bitmapToByteArray(bitmap)
                collData.frameColorString = Base64.encodeToString(byteArr, Base64.NO_WRAP)

                val stringDepth = Base64.encodeToString(collData.depthData, Base64.NO_WRAP)
                collData.frameDepthString = stringDepth

                collData.colorData = byteArr
                collData.isRepaired = true
                collData.pointData = formatDataFaceHeader(collData.dataFacePoint!!.dataFace!!) ?: ""
                collData
            } else {
                null
            }
        } else {
            collData
        }
    }

    /**
     * format string data face
     */
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
     * draw facePointData on bitmap [drawFaceDataToBitmap]
     */
    fun drawFaceDataToBitmap(facePointData: FacePointData, faceBitmap: Bitmap): Bitmap {
        val workingBitmap: Bitmap = Bitmap.createBitmap(faceBitmap)
        val mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        canvas.drawPoint(
                facePointData.LeftEye.x.toFloat(),
                facePointData.LeftEye.y.toFloat(),
                paint
        )
        canvas.drawPoint(
                facePointData.RightEye.x.toFloat(),
                facePointData.RightEye.y.toFloat(),
                paint
        )
        canvas.drawPoint(
                facePointData.Leftmouth.x.toFloat(),
                facePointData.Leftmouth.y.toFloat(),
                paint
        )
        canvas.drawPoint(
                facePointData.Rightmouth.x.toFloat(),
                facePointData.Rightmouth.y.toFloat(),
                paint
        )
        canvas.drawPoint(facePointData.Nose.x.toFloat(), facePointData.Nose.y.toFloat(), paint)

        canvas.drawRect(facePointData.faceRect, paint)
        canvas.save()
        return mutableBitmap
    }

    /**
     * convert image to nv21
     */
    private fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize
        var a: Int
        var R: Int
        var G: Int
        var B: Int
        var Y: Int
        var U: Int
        var V: Int
        var index = 0
        for (j in 0 until height) {
            for (i in 0 until width) {
                //a = argb[index] and -0x1000000 shr 24
                R = argb[index] and 0xff0000 shr 16
                G = argb[index] and 0xff00 shr 8
                B = argb[index] and 0xff shr 0
                Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
                U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
                V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128
                yuv420sp[yIndex++] =
                        (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] =
                            (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                    yuv420sp[uvIndex++] =
                            (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                }
                index++
            }
        }
    }


    fun getNV21(inputWidth: Int, inputHeight: Int, scaled: Bitmap): ByteArray {
        val argb = IntArray(inputWidth * inputHeight)
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)
        val yuv = ByteArray(inputWidth * inputHeight * 3 / 2)
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight)
        scaled.recycle()
        return yuv
    }

    /**
     * check position face [getCenterPoint], [distancePoint], [getFaceDegreeY], [getFaceDegreeX], [getFaceDegreeY]
     */
    fun getCenterPoint(point1: Point, point2: Point): Point {
        val disX = abs((point1.x - point2.x)) / 2
        val x = if (point1.x > point2.x) {
            point2.x + disX
        } else {
            point1.x + disX
        }
        val disY = abs((point1.y - point2.y)) / 2
        val y = if (point1.y > point2.y) {
            point2.y + disY
        } else {
            point1.y + disY
        }
        return Point(x, y)
    }

    fun distancePoint(a: Point, b: Point): Float {
        return sqrt(
                (a.x.toDouble() - b.x.toDouble()).pow(2.0) + (a.y.toDouble() - b.y.toDouble()).pow(
                        2.0
                )
        ).toFloat()
    }

    fun getFaceDegreeY(facePointData: FacePointData): Float {
        return getNumberToFaceDegreeY(
                facePointData.LeftEye, facePointData.RightEye,
                facePointData.Nose, facePointData.Leftmouth, facePointData.Rightmouth
        )
    }

    fun getNumberToFaceDegreeY(
            pointEyeLeft: Point,
            pointEyeRight: Point,
            pointNose: Point,
            pointMouthLeft: Point,
            pointMouthRight: Point
    ): Float {
        val pointCenterEye = getCenterPoint(pointEyeLeft, pointEyeRight)
        val pointCenterMouth = getCenterPoint(pointMouthLeft, pointMouthRight)
        val pointCenterY = getCenterPoint(pointCenterEye, pointCenterMouth)
        val rY = distancePoint(pointCenterEye, pointCenterY)
        val disOMY = distancePoint(Point(pointCenterY.x, pointNose.y), pointCenterY)
        val angleDataY = disOMY / rY
        val angleY = acos(angleDataY)
        return if (pointNose.y < pointCenterY.y) (90 - angleY * (180 / PI).toFloat()) else -(90 - angleY * (180 / PI).toFloat())
    }

    fun getFaceDegreeX(facePointData: FacePointData): Float {
        return getNumberToFaceDegreeX(
                facePointData.LeftEye, facePointData.RightEye,
                facePointData.Nose, facePointData.Leftmouth, facePointData.Rightmouth
        )
    }

    fun getNumberToFaceDegreeX(
            pointEyeLeft: Point,
            pointEyeRight: Point,
            pointNose: Point,
            pointMouthLeft: Point,
            pointMouthRight: Point
    ): Float {
        val pointCenterEyeMouthLeft = getCenterPoint(pointEyeLeft, pointMouthLeft)
        val pointCenterEyeMouthRight = getCenterPoint(pointEyeRight, pointMouthRight)
        val pointCenterX = getCenterPoint(pointCenterEyeMouthLeft, pointCenterEyeMouthRight)
        val rX = distancePoint(pointCenterEyeMouthLeft, pointCenterX)
        val disOMX = distancePoint(Point(pointNose.x, pointCenterEyeMouthLeft.y), pointCenterX)
        val angleDataX = disOMX / rX
        val angleX = acos(angleDataX)
        return if (pointNose.x > pointCenterX.x) (90 - angleX * (180 / PI).toFloat()) else -(90 - angleX * (180 / PI).toFloat())
    }

    /**
     * get a face to list face [getLargestFace]
     */
    fun getLargestFace(faces: Vector<Box>): Box? {
        var largest: Box? = null
        return if (faces.isNotEmpty()) {
            faces.forEach {
                if (largest == null) {
                    largest = it
                } else if (largest!!.width() < it.width()) {
                    largest = it
                }
            }
            largest
        } else {
            largest
        }
    }

    /**
     * check Zone face [checkZoneFace]
     */
    fun checkZoneFace(face: Box): Boolean {
        val left = face.box[0]
        val right = face.box[2]
        val top = face.box[1]
        val bot = face.box[3]
        val x = (left + right) * 0.5f
        val y = (top + bot) * 0.5f
        Log.e("checkZoneFaceasdsadas", "$x")
        return x in Detection.zoneFaceX && y in Detection.zoneFaceY
    }

    /**
     * QR code [decryptQRCode]
     */
    fun decryptQRCode(token: String): JsonObject? {
        try {
            val pattern2 =
                    Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?\$") //  check string is Base64
            val m2 = pattern2.matcher(token)
            if (m2.matches()) {
                val qr = Base64.decode(token, Base64.NO_PADDING)
                val value: String
                if (qr.size % 16 == 0) {
                    val prepareKey = Base64.decode(Key.PREPARE_KEY, Base64.NO_PADDING)
                    val objectData = Crypto.aesDecryptCBC(prepareKey, qr)
                    value = String(objectData)
                    Log.e("decryptQRCode", value)
                } else {
                    return null
                }
                return try {
                    Gson().fromJson(value, JsonObject::class.java)
                } catch (e: Exception) {
                    null
                }
            } else {
                return null
            }
        } catch (e: java.lang.Exception) {
            return null
        }
    }

}