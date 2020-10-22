package wee.digital.camera.detector

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import wee.digital.camera.*

class FaceDetector {

    companion object {
        const val MIN_DISTANCE = 120
        const val MIN_SIZE = 300
    }

    private val maskFilter = ModelFilter("face/mask/manifest.json")

    private val depthFilter = ModelFilter("face/depth/manifest.json")

    private val mtcnn: MTCNN = MTCNN(RealSense.app.assets)

    private var currentFace: Box? = null

    private var isDetecting: Boolean = false

    var dataListener: DataListener? = null

    var statusListener: StatusListener? = null

    var optionListener: OptionListener = object : OptionListener {}

    fun release() {
        currentFace = null
    }

    fun detectFace(colorBitmap: Bitmap, depthBitmap: Bitmap) {
        if (isDetecting) return
        isDetecting = true
        mtcnn.detectFacesAsync(colorBitmap, MIN_SIZE)
                .addOnCompleteListener { isDetecting = false }
                .addOnCanceledListener { isDetecting = false }
                .addOnFailureListener { statusListener?.onFaceLeaved() }
                .addOnCompleteListener { task ->
                    val box: Box? = task.result.largestBox()
                    if (box == null) {
                        statusListener?.onFaceLeaved()
                    } else {
                        statusListener?.onFacePerformed()
                        if (!faceChangeProcess(box)) statusListener?.onFaceChanged()
                        currentFace = box

                        onFaceDetect(box, colorBitmap, depthBitmap)
                    }
                }
    }

    fun destroy() {
        depthFilter.destroy()
        maskFilter.destroy()
    }


    /**
     * Detect method 1st: use [OptionListener] filter face properties to continue [onMaskDetect]
     */
    private fun onFaceDetect(box: Box, colorBitmap: Bitmap, depthBitmap: Bitmap) {

        if (!optionListener.onFaceScore(box.score)) return

        if (!optionListener.onFaceRect(box.left(), box.top(), box.faceWidth(), box.faceHeight())) {
            return
        }

        var degreesValid = false
        box.getDegrees { x, y -> degreesValid = optionListener.onFaceDegrees(x, y) }
        if (!degreesValid) return

        val boxRect = box.transformToRect()

        onMaskDetect(boxRect, colorBitmap, depthBitmap)
    }


    /**
     * Detect method 2nd: use [maskFilter] crop face color image
     * and get vision label to continue [onDepthDetect]
     */
    private fun onMaskDetect(boxRect: Rect, colorBitmap: Bitmap, depthBitmap: Bitmap) {

        val faceBitmap = boxRect.cropColorFace(colorBitmap) ?: return
        dataListener?.onFaceColorImage(faceBitmap)

        maskFilter.processImage(faceBitmap) { text, confidence ->
            text ?: return@processImage
            if (optionListener.onMaskLabel(text, confidence)) {
                onDepthDetect(boxRect, colorBitmap, depthBitmap)
            }
        }
    }

    /**
     * Detect method 3rd: use [depthFilter] crop face depth image
     * and get vision label to continue [onGetPortrait]
     */
    private fun onDepthDetect(boxRect: Rect, colorBitmap: Bitmap, depthBitmap: Bitmap) {

        val faceBitmap = if (RealSense.DEPTH_WIDTH != RealSense.COLOR_WIDTH) {
            boxRect.cropDepthFace(depthBitmap) ?: return
        } else {
            boxRect.cropColorFace(depthBitmap) ?: return
        }
        dataListener?.onFaceDepthImage(faceBitmap)

        depthFilter.processImage(faceBitmap) { text, confidence ->
            text ?: return@processImage
            if (optionListener.onDepthLabel(text, confidence)) {
                onGetPortrait(boxRect, colorBitmap)
            }
        }
    }

    /**
     * Detect method 4th: detected face portrait
     */
    private fun onGetPortrait(boxRect: Rect, bitmap: Bitmap) {
        boxRect.cropPortrait(bitmap)?.also {
            dataListener?.onPortraitImage(it)
        }
    }

    private fun faceChangeProcess(face: Box): Boolean {
        currentFace ?: return false
        val nowRect = face.transformToRect()
        val nowCenterX = nowRect.exactCenterX()
        val nowCenterY = nowRect.exactCenterY()
        val nowCenterPoint = PointF(nowCenterX, nowCenterY)
        val curRect = currentFace!!.transformToRect()
        val curCenterX = curRect.exactCenterX()
        val curCenterY = curRect.exactCenterY()
        val curCenterPoint = PointF(curCenterX, curCenterY)
        val dist = distancePoint(nowCenterPoint, curCenterPoint)
        return dist < MIN_DISTANCE
    }


    /**
     * Callback methods return full size image, face crop image of color image & depth image
     */
    interface DataListener {

        fun onFaceColorImage(bitmap: Bitmap?) {}

        fun onFaceDepthImage(bitmap: Bitmap?) {}

        fun onPortraitImage(bitmap: Bitmap)
    }

    /**
     * Callback methods when detect on a pair of color image & depth image
     */
    interface StatusListener {

        fun onFacePerformed()

        fun onFaceLeaved()

        fun onFaceChanged() {}

    }

    /**
     * Face detector option filter to get a portrait image if all method return true
     */
    interface OptionListener {

        fun onMaskLabel(label: String, confidence: Float): Boolean {
            return label == "face_chip"
        }

        fun onDepthLabel(label: String, confidence: Float): Boolean {
            return label == "real"
        }

        fun onFaceScore(score: Float): Boolean {
            return true
        }

        fun onFaceRect(left: Int, top: Int, width: Int, height: Int): Boolean {
            return true
        }

        fun onFaceDegrees(x: Double, y: Double): Boolean {
            return true
        }

    }

}