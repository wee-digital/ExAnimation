package wee.digital.fpa.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions

class Detection(context: Context) {

    companion object {
        var sizeFace = 100
        var zoneFaceX = 220f..420f
        var zoneFaceY = 205f..350f
        var leftCorner = 77
        var topCorner = 60
        var scale = 0.76
    }

    private val ct: Context = context

    private var countFaceNull = 0

    private var countFaceOk = 0

    private var isBitmapChecking = false

    private var isCheckDepthFace = false

    private var mtcnn: MTCNN? = null

    private var mDepthLabeler: FirebaseVisionImageLabeler? = null

    private var listener: DetectionCallBack? = null

    private var handlerDetection: Handler? = null

    private var handlerThreadDetection: HandlerThread? = null

    init {
        initDetection()
    }

    private fun initDetection() {
        handlerThreadDetection = HandlerThread("DetectionThread")
        handlerThreadDetection?.start()
        handlerDetection = Handler(handlerThreadDetection?.looper!!)
        try {
            mtcnn = MTCNN(ct.assets)

            val localModelDepthCheck =
                FirebaseAutoMLLocalModel.Builder().setAssetFilePath("camera/manifest.json").build()
            val labelerDepthCheckOptions =
                FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModelDepthCheck)
                    .setConfidenceThreshold(0.5f)
                    .build()

            mDepthLabeler =
                FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerDepthCheckOptions)
        } catch (e: Exception) {
            Log.e("initNTCNN", "Error: ${e.message}")
        }
    }

    fun bitmapChecking(frameColor: Bitmap?, frameDepth: ByteArray?, dataCollect: DataCollect) {
        frameColor ?: return
        frameDepth ?: return

        if (isBitmapChecking) return
        isBitmapChecking = true

        val bitmapColor = frameColor.copy(frameColor.config, true)
        if (bitmapColor == null) {
            isBitmapChecking = false
            return
        }

        mtcnn?.detectFacesAsync(bitmapColor, sizeFace)

            ?.addOnSuccessListener {
                handlerDetection?.post {
                    val face = FrameUtil.getLargestFace(it)
                    if (face != null) {
                        detectFace(face, bitmapColor, frameDepth, dataCollect)
                    } else {
                        checkingFaceNull()
                    }
                }
            }
            ?.addOnFailureListener {
                listener?.faceNull()
            }
    }

    private fun detectFace(
        box: Box,
        bmColor: Bitmap,
        frameDepth: ByteArray,
        dataCollect: DataCollect
    ) {
        handlerDetection?.post {
            if (box.score > 0.99 && FrameUtil.checkZoneFace(box)) {
                listener?.hasFace()

                val byteFullFace = BitmapUtils.bitmapToByteArray(bmColor)

                val bitmapDepth = FrameUtil.getBitmapFromByte(
                    frameDepth,
                    RealSenseControl.COLOR_WIDTH,
                    RealSenseControl.COLOR_HEIGHT
                )

                val rectDepthFace = FrameUtil.getRectDepthFace(box.transform2Rect())

                val cropDepthBitmap =
                    FrameUtil.cropBitmapWithRect(bitmapDepth, rectDepthFace)

                val data = FrameUtil.getDataFaceAndFace(bmColor, box)

                if (cropDepthBitmap != null && data.face != null && byteFullFace != null && data.dataFace != null) {
                    dataCollect.dataFacePoint = data
                    checkFaceFake(
                        cropDepthBitmap,
                        data.face,
                        byteFullFace,
                        data.dataFace,
                        dataCollect
                    )
                } else {
                    checkingFaceNull()
                }
                isBitmapChecking = false
            } else {
                checkingFaceNull()
            }
        }
    }

    private fun checkingFaceNull() {
        isBitmapChecking = false

        countFaceNull++
        if (countFaceNull > 3) {
            countFaceNull = 0

            listener?.faceNull()
        }
    }

    private fun checkFaceFake(
            faceCheck: Bitmap,
            faceCrop: ByteArray,
            frameFullFace: ByteArray,
            faceData: FacePointData,
            dataCollect: DataCollect
    ) {
        if (isCheckDepthFace) return
        isCheckDepthFace = true

        handlerDetection?.post {
            val faceDegreeX = FrameUtil.getFaceDegreeX(faceData)

            val faceDegreeY = FrameUtil.getFaceDegreeY(faceData)

            val checkDegree = faceDegreeX in -35f..35f && faceDegreeY in -35f..35f

            if (checkDegree) {
                val image = FirebaseVisionImage.fromBitmap(faceCheck)

                mDepthLabeler?.processImage(image)
                    ?.addOnSuccessListener { labels ->
                        faceCheck.recycle()
                        checkLabels(labels, faceCrop, frameFullFace, faceData, dataCollect)
                    }
                    ?.addOnFailureListener {
                        faceCheck.recycle()
                        checkFaceFakeNull()
                    }
            } else {
                faceCheck.recycle()
                checkFaceFakeNull()
            }
        }
    }

    private fun checkLabels(
            labels: List<FirebaseVisionImageLabel>?,
            face: ByteArray,
            fullFace: ByteArray,
            faceData: FacePointData,
            dataCollect: DataCollect
    ) {
        if (labels.isNullOrEmpty()) {
            checkFaceFakeNull()

            return
        }

        if (labelStatusFace(labels) == "real") {
            Log.e("Detection", "get face ok")
            countFaceOk++
            if (countFaceOk > 2) {
                listener?.faceEligible(face, fullFace, faceData, dataCollect)

                countFaceOk = 0
                countFaceNull = 0
            }
            isCheckDepthFace = false
        } else {
            checkFaceFakeNull()
        }
    }

    private fun checkFaceFakeNull() {
        isCheckDepthFace = false

        countFaceNull++
        if (countFaceNull > 2) {
            countFaceNull = 0
            listener?.faceNull()
        }
    }

    fun destroyThread() {
        handlerThreadDetection?.quitSafely()

        mDepthLabeler?.close()
        mDepthLabeler = null

        handlerDetection = null

        mtcnn = null

        try {
            handlerThreadDetection?.join()
        } catch (e: Exception) {
        }
    }

    fun labelStatusFace(data: List<FirebaseVisionImageLabel>): String {
        return try {
            data.first().text
        } catch (e: java.lang.Exception) {
            e.message.toString()
        }
    }

    fun initCallBack(l: DetectionCallBack) {
        listener = l
    }

    interface DetectionCallBack {
        fun faceNull()
        fun hasFace()
        fun faceEligible(
                bm: ByteArray,
                frameFullFace: ByteArray,
                faceData: FacePointData,
                dataCollect: DataCollect
        )
    }
}