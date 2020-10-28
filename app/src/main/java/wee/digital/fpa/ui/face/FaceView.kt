package wee.digital.fpa.ui.face

import android.graphics.Bitmap
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import kotlinx.android.synthetic.main.face.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.Detection
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.util.observerCamera
import wee.digital.library.extension.*


class FaceView(private val v: FaceFragment) : Detection.DetectionCallBack {

    companion object {
        const val ANIM_DURATION = 400L
    }

    private var mDetection: Detection? = null

    private var hasFaceDetect = true

    var onFaceEligible: (ByteArray, FacePointData, DataCollect) -> Unit = { _, _, _ -> }

    private var hasFace: Boolean = false

    private val viewTransition = ChangeBounds().apply {
        duration = ANIM_DURATION
    }

    private fun onConfigFaceReg() {
        mDetection = Detection(v.requireActivity()).also {
            it.initCallBack(this)
        }
        App.realSenseControl?.listener = object : RealSenseControl.Listener {
            override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
                colorBitmap ?: return
                dataCollect ?: return
                v.requireActivity().runOnUiThread {
                    v.faceImageViewCamera?.setImageBitmap(colorBitmap)
                }
                if (!hasFaceDetect) return
                mDetection?.bitmapChecking(colorBitmap, depthBitmap, dataCollect)
            }

        }
    }

    private fun animateImageScale(scale: Float) {
        v.faceImageViewCamera.apply {
            animate().scaleX(scale).scaleY(scale).duration = ANIM_DURATION
        }
        v.faceImageViewAnim.apply {
            animate().scaleX(scale).scaleY(scale).duration = ANIM_DURATION
        }
    }

    fun onViewInit() {
        v.faceImageViewAnim.load(R.mipmap.img_progress)
        v.observerCamera()
        onConfigFaceReg()
    }

    fun onBindRemainingText(second: Int) {
        if (second > 0) {
            val sHour = "%02d:%02d".format(second / 60, second % 60).bold()
            val sRemaining = "Thời gian còn lại: %s".format(sHour)
            v.faceTextViewRemaining.setHyperText(sRemaining)
        } else {
            v.faceTextViewRemaining.text = null
        }
    }

    fun animateOnFaceCaptured() {
        val viewId = v.faceImageViewCamera.id
        val faceHeight = (v.faceImageViewCamera.height / 2.23).toInt()
        val scale = faceHeight / v.faceImageViewCamera.height.toFloat()
        viewTransition.beginTransition(v.viewContent) {
            setAlpha(v.faceTextViewTitle1.id, 0f)
            setAlpha(v.faceTextViewTitle2.id, 0f)
            setAlpha(v.faceTextViewTitle3.id, 0f)
            connect(viewId, ConstraintSet.TOP, v.faceTextViewRemaining.id, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.BOTTOM, v.guidelineFace.id, ConstraintSet.BOTTOM)
        }
        animateImageScale(scale)
    }

    fun animateOnStartFaceReg() {
        val viewId = v.faceImageViewCamera.id
        val scale = 1f
        viewTransition.beginTransition(v.viewContent, {
            clear(viewId, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.TOP, v.faceGuidelineCameraTop.id, ConstraintSet.TOP)
        }, {
            setAlpha(v.faceTextViewTitle1.id, 1f)
            setAlpha(v.faceTextViewTitle2.id, 1f)
            setAlpha(v.faceTextViewTitle3.id, 1f)
            hasFaceDetect = true
        })
        animateImageScale(scale)
    }


    /**
     * [Detection.DetectionCallBack] implement
     */
    override fun faceNull() {
        if (hasFace) {
            hasFace = false
            v.faceImageViewAnim.post {
                viewTransition.beginTransition(v.viewContent) {
                    setAlpha(v.faceImageViewAnim.id, 0f)
                }
            }
        }
    }

    override fun hasFace() {
        if (!hasFace) {
            hasFace = true
            v.faceImageViewAnim.post {
                viewTransition.beginTransition(v.viewContent) {
                    setAlpha(v.faceImageViewAnim.id, 1f)
                }
            }
        }
    }

    override fun faceEligible(bm: ByteArray, portrait: Bitmap, frameFullFace: ByteArray, faceData: FacePointData, dataCollect: DataCollect) {
        hasFaceDetect = false
        v.faceImageViewCamera.setImageBitmap(portrait)
        onFaceEligible(bm, faceData, dataCollect)
    }


}