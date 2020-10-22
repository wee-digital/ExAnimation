package wee.digital.camera

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import wee.digital.camera.detector.Box
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.math.*


fun debug(s: Any?) {
    if (BuildConfig.DEBUG) Log.d("Camera", s.toString())
}

val uiHandler: Handler get() = Handler(Looper.getMainLooper())

val isOnUiThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()

fun uiThread(block: () -> Unit) {
    if (isOnUiThread) block()
    else uiHandler.post { block() }
}

fun uiThread(delay: Long, block: () -> Unit) {
    uiHandler.postDelayed({ block() }, delay)
}

fun FragmentActivity?.addFragment(
        fragment: Fragment, @IdRes container: Int,
        backStack: Boolean = true,
        animations: IntArray? = null
) {
    this ?: return
    val tag = fragment::class.java.simpleName
    supportFragmentManager.scheduleTransaction({
        add(container, fragment, tag)
        if (backStack) addToBackStack(tag)
    }, animations)
}

fun FragmentActivity?.replaceFragment(
        fragment: Fragment, @IdRes container: Int,
        backStack: Boolean = true,
        animations: IntArray? = null
) {
    this ?: return
    val tag = fragment::class.java.simpleName
    supportFragmentManager.scheduleTransaction({
        replace(container, fragment, tag)
        if (backStack) addToBackStack(tag)
    }, animations)
}

fun FragmentActivity?.isExist(cls: Class<*>): Boolean {
    this ?: return false
    val tag = cls.simpleName
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    return null != fragment
}

fun FragmentActivity?.isNotExist(cls: Class<*>): Boolean {
    this ?: return false
    val tag = cls.simpleName
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    return null == fragment
}

fun FragmentActivity?.remove(cls: Class<*>, animations: IntArray? = null) {
    remove(cls.simpleName, animations)
}

fun FragmentActivity?.remove(tag: String?, animations: IntArray? = null) {
    this ?: return
    tag ?: return
    val fragment = supportFragmentManager.findFragmentByTag(tag) ?: return
    supportFragmentManager.scheduleTransaction({
        remove(fragment)
    }, animations)
}

fun FragmentManager.scheduleTransaction(
        block: FragmentTransaction.() -> Unit,
        animations: IntArray? = null
) {

    val transaction = beginTransaction()
    if (null != animations) transaction.setCustomAnimations(
            animations[0],
            animations[1],
            animations[2],
            animations[3]
    )
    transaction.block()
    transaction.commitAllowingStateLoss()

}


/**
 * Box
 */
fun Vector<Box>?.largestBox(): Box? {
    if (this.isNullOrEmpty()) return null
    var largestFace: Box? = null
    this.forEach {
        if (largestFace == null) {
            largestFace = it
        } else if (largestFace!!.width() < it.width()) {
            largestFace = it
        }
    }
    return largestFace
}

fun Box.faceWidth(): Int {
    val right = this.box[2]
    val left = this.box[0]
    return right - left
}

fun Box.faceHeight(): Int {
    val bottom = this.box[3]
    val top = this.box[1]
    return bottom - top
}

fun Box.getDegrees(block: (Double, Double) -> Unit) {
    val rect = this.landmark
    val leftEye = rect[0]
    val rightEye = rect[1]
    val nose = rect[2]
    val rightMouth = rect[4]
    val leftMouth = rect[3]
    val x = getFaceDegreeX(leftEye, rightEye, nose, leftMouth, rightMouth)
    val y = getFaceDegreeY(leftEye, rightEye, nose, leftMouth, rightMouth)
    block(x, y)
}

fun Rect.getRectCrop(bitmap: Bitmap): Rect {
    val top = if (top < 0) 0 else top
    val left = if (left < 0) 0 else left
    val right = if (right > bitmap.width) bitmap.width else right
    val bottom = if (bottom > bitmap.height) bitmap.height else bottom
    return Rect(left, top, right, bottom)
}


/**
 * Crop face portrait image
 * @this: box.transformToRect
 */
fun Rect.cropPortrait(bitmap: Bitmap): Bitmap? {

    val plusH = height() * 0.15
    val plusW = width() * 0.15
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


/**
 * Crop face color image
 * @this: box.transformToRect
 */
fun Rect.cropColorFace(bitmap: Bitmap): Bitmap? {
    val rect = this.getRectCrop(bitmap)
    val copiedBitmap = bitmap.copy(Bitmap.Config.RGB_565, true)
    return try {
        Bitmap.createBitmap(copiedBitmap, rect.left, rect.top, rect.width(), rect.height())
    } catch (ex: Exception) {
        Log.e("cropFace", "${ex.message}")
        null
    }
}


/**
 * Crop face depth image
 * @this: box.transformToRect
 */
fun Rect.cropDepthFace(bitmap: Bitmap): Bitmap? {
    val rect = this.getFace1280x720()
    var top = rect.top
    if (top < 0) {
        top = 0
    }
    var left = rect.left
    if (left < 0) {
        left = 0
    }
    val height = rect.height()
    val width = rect.width()
    var x = left
    var y = top
    if (x < 0) x = 0
    if (y < 0) y = 0

    return try {
        val cropBitmap = Bitmap.createBitmap(bitmap, x, y, width, height)
        cropBitmap
    } catch (ex: Exception) {
        null
    }
}

fun Rect.getFace1920x1080(): Rect {
    val leftCorner = 1
    val topCorner = 35
    val scale = 0.36
    val x = this.exactCenterX() * scale + leftCorner / scale
    val y = this.exactCenterY() * scale + topCorner
    val width = this.width() * scale
    val height = this.height() * scale
    val left = x - width / 2
    val top = y - height / 2
    val right = x + width / 2
    val bottom = y + height / 2
    return Rect(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
}

fun Rect.getFace1280x720(): Rect {
    val leftCorner = -13
    val topCorner = 29
    val scale = 0.58
    val x = this.exactCenterX() * scale + leftCorner / scale
    val y = this.exactCenterY() * scale + topCorner
    val width = this.width() * scale
    val height = this.height() * scale
    val left = x - width / 2
    val top = y - height / 2
    val right = x + width / 2
    val bottom = y + height / 2
    return Rect(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
}

fun Rect.getFace640x320(): Rect {
    val leftCorner = 77
    val topCorner = 60
    val scale = 0.76
    val x = this.exactCenterX() * scale + leftCorner / scale
    val y = this.exactCenterY() * scale + topCorner
    val width = this.width() * scale
    val height = this.height() * scale
    val left = x - width / 2
    val top = y - height / 2
    val right = x + width / 2
    val bottom = y + height / 2
    return Rect(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
}


/**
 * Utils
 */
private fun getFaceDegreeY(
        pointEyeLeft: Point,
        pointEyeRight: Point,
        pointNose: Point,
        pointMouthLeft: Point,
        pointMouthRight: Point
): Double {
    val pointCenterEye = getCenterPoint(pointEyeLeft, pointEyeRight)
    val pointCenterMouth = getCenterPoint(pointMouthLeft, pointMouthRight)
    val pointCenterY = getCenterPoint(pointCenterEye, pointCenterMouth)
    val rY = distancePoint(pointCenterEye, pointCenterY)
    val disOMY = distancePoint(Point(pointCenterY.x, pointNose.y), pointCenterY)
    val angleDataY = disOMY / rY
    val angleY = acos(angleDataY.toDouble())
    return if (pointNose.y < pointCenterY.y) (90 - angleY * (180 / Math.PI).toFloat()) else -(90 - angleY * (180 / Math.PI).toFloat())
}

private fun getFaceDegreeX(
        pointEyeLeft: Point,
        pointEyeRight: Point,
        pointNose: Point,
        pointMouthLeft: Point,
        pointMouthRight: Point
): Double {
    val pointCenterEyeMouthLeft = getCenterPoint(pointEyeLeft, pointMouthLeft)
    val pointCenterEyeMouthRight = getCenterPoint(pointEyeRight, pointMouthRight)
    val pointCenterX = getCenterPoint(pointCenterEyeMouthLeft, pointCenterEyeMouthRight)
    val rX = distancePoint(pointCenterEyeMouthLeft, pointCenterX)
    val disOMX = distancePoint(Point(pointNose.x, pointCenterEyeMouthLeft.y), pointCenterX)
    val angleDataX = disOMX / rX
    val angleX = acos(angleDataX.toDouble())
    return if (pointNose.x > pointCenterX.x) (90 - angleX * (180 / Math.PI).toFloat()) else -(90 - angleX * (180 / Math.PI).toFloat())
}

private fun getCenterPoint(point1: Point, point2: Point): Point {
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

fun distancePoint(p1: PointF, p2: PointF): Float {
    val dist = sqrt(
            (p2.x - p1.x).toDouble().pow(2)
                    + (p2.y - p1.y).toDouble().pow(2)
    )
    return dist.toFloat()
}

/**
 * Image utils
 */

fun ByteArray?.rgbToArgb(width: Int, height: Int): IntArray? {
    this ?: return null
    try {
        val frameSize = width * height
        val rgb = IntArray(frameSize)
        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val colorSpace = index * 3
                val r = this[colorSpace + 2].toInt()
                val g = this[colorSpace + 1].toInt()
                val b = this[colorSpace].toInt()
                rgb[index] = (r and 0xff) or (g and 0xff shl 8) or (b and 0xff shl 16)
                index++
            }
        }
        return rgb
    } catch (e: Throwable) {
        return null
    }

}

fun IntArray?.argbToBitmap(width: Int, height: Int): Bitmap? {
    this ?: return null
    return try {
        val bmp = Bitmap.createBitmap(this, width, height, Bitmap.Config.RGB_565)
        return bmp.flipHorizontal()
    } catch (e: Throwable) {
        null
    }
}

fun ByteArray?.rgbToBitmap(width: Int, height: Int): Bitmap? {
    return this.rgbToArgb(width, height).argbToBitmap(width, height)
}

fun ByteArray?.getDepthBitmap(): Bitmap? {
    return rgbToBitmap(RealSense.DEPTH_WIDTH, RealSense.DEPTH_HEIGHT)
}

fun ByteArray?.getColorBitmap(): Bitmap? {
    return rgbToBitmap(RealSense.COLOR_WIDTH, RealSense.COLOR_HEIGHT)
}

fun View.scaleToFrameRatio() {
    ConstraintSet().also {
        it.clone(parent as ConstraintLayout)
        val ratio = if (layoutParams.width == 0) "w,${RealSense.COLOR_WIDTH}:${RealSense.COLOR_HEIGHT}"
        else "w,${RealSense.COLOR_HEIGHT}:${RealSense.COLOR_WIDTH}"
        it.setDimensionRatio(this.id, ratio)
        it.applyTo(parent as ConstraintLayout)
    }
}

/**
 * Bitmap utils
 */
fun Bitmap.flipHorizontal(): Bitmap? {
    return try {
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    } catch (e: Throwable) {
        null
    } finally {
        recycle()
    }
}

fun Bitmap?.toBytes(): ByteArray {
    this ?: return ByteArray(1)
    return try {
        val stream = ByteArrayOutputStream()
        this.copy(Bitmap.Config.RGB_565, true)?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        stream.close()
        byteArray
    } catch (e: Exception) {
        ByteArray(1)
    }
}