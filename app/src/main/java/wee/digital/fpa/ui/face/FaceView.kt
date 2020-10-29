package wee.digital.fpa.ui.face

import android.graphics.Bitmap
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import kotlinx.android.synthetic.main.face.*
import wee.digital.fpa.R
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.Detection
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.util.observerCameraListener
import wee.digital.library.extension.beginTransition
import wee.digital.library.extension.bold
import wee.digital.library.extension.load
import wee.digital.library.extension.setHyperText


class FaceView(private val v: FaceFragment) :
        RealSenseControl.Listener,
        Detection.DetectionCallBack {

    companion object {
        const val ANIM_DURATION = 400L
    }

    /**
     * [RealSenseControl.Listener] implement
     */
    override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
        colorBitmap ?: return
        dataCollect ?: return
        if (!hasFaceDetect) return
        v.requireActivity().runOnUiThread {
            v.faceImageViewCamera?.setImageBitmap(colorBitmap)
        }
        mDetection?.bitmapChecking(colorBitmap, depthBitmap, dataCollect)
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

    /**
     * [FaceView] properties
     */
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
        v.observerCameraListener(this)
        onConfigFaceReg()
    }

    fun onBindRemainingText(second: Int) {
        if (second < 0) {
            v.faceTextViewRemaining.text = null
            return
        }
        val sHour = "%02d:%02d".format(second / 60, second % 60).bold()
        val sRemaining = "Thời gian còn lại: %s".format(sHour)
        v.faceTextViewRemaining.setHyperText(sRemaining)
    }

    fun animateOnFaceCaptured() {
        val viewId = v.faceImageViewCamera.id
        val faceHeight = (v.faceImageViewCamera.height / 2.23).toInt()
        val scale = faceHeight / v.faceImageViewCamera.height.toFloat()
        //v.faceImageViewCamera.setBackgroundResource(0)
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
        //v.faceImageViewCamera.setBackgroundResource(R.drawable.drw_face)
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




}