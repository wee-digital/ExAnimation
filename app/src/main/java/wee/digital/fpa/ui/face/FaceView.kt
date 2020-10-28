package wee.digital.fpa.ui.face

import android.graphics.Bitmap
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.face.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.Detection
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.fpa.util.SimpleTransitionListener
import wee.digital.library.extension.bold
import wee.digital.library.extension.load
import wee.digital.library.extension.setHyperText


class FaceView(private val v: FaceFragment) : Detection.DetectionCallBack {

    private var mDetection: Detection? = null

    var hasFaceDetect = true

    private var hasFace: Boolean = false

    private val animDuration: Long = 400

    private val viewTransition = ChangeBounds().apply {
        duration = animDuration
    }

    private fun onLifecycleObserve() {
        v.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onPause() {
                hasFaceDetect = false
                App.realSenseControl?.listener = null
            }

            override fun onDestroy() {
                App.realSenseControl?.stopStreamThread()
            }
        })
    }

    private fun onStartCamera() {
        RsContext.init(v.requireContext())
        UsbUtilities.grantUsbPermissionIfNeeded(v.requireContext())
        App.realSenseControl?.startStreamThread()
        mDetection = Detection(v.requireActivity()).also {
            it.initCallBack(this)
        }
        App.realSenseControl?.listener = object : RealSenseControl.Listener {

            override fun onCameraStarted() {}

            override fun onCameraError(mess: String) {}

            override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
                colorBitmap ?: return
                dataCollect ?: return

                if (!hasFaceDetect) return
                v.requireActivity().runOnUiThread {
                    v.faceImageViewCamera?.setImageBitmap(colorBitmap)
                }
                mDetection?.bitmapChecking(colorBitmap, depthBitmap, dataCollect)
            }

        }
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

        viewTransition.duration = animDuration
        onViewAnimate {
            //setAlpha(v.faceImageViewAnim.id, 1f)
            setAlpha(v.faceTextViewTitle1.id, 0f)
            setAlpha(v.faceTextViewTitle2.id, 0f)
            setAlpha(v.faceTextViewTitle3.id, 0f)
            connect(viewId, ConstraintSet.TOP, v.faceTextViewRemaining.id, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.BOTTOM, v.guidelineFace.id, ConstraintSet.BOTTOM)
        }
        animateImageScale(scale)
    }

    fun animateOnStartFaceReg(onEnd: () -> Unit) {
        val viewId = v.faceImageViewCamera.id
        val faceHeight = (v.faceImageViewCamera.height * 2.23).toInt()
        val scale = 1f

        viewTransition.addListener(object : SimpleTransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                viewTransition.removeListener(this)
                onViewAnimate {
                    setAlpha(v.faceTextViewTitle1.id, 1f)
                    setAlpha(v.faceTextViewTitle2.id, 1f)
                    setAlpha(v.faceTextViewTitle3.id, 1f)
                }
                onEnd()
            }
        })
        viewTransition.duration = animDuration
        onViewAnimate {
            clear(viewId, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.TOP, v.faceGuidelineCameraTop.id, ConstraintSet.TOP)
        }
        animateImageScale(scale)
    }

    private fun onViewAnimate(block: ConstraintSet.() -> Unit) {
        TransitionManager.beginDelayedTransition(v.viewContent, viewTransition)
        ConstraintSet().also {
            it.clone(v.viewContent)
            it.block()
            it.applyTo(v.viewContent)
        }
    }

    private fun animateImageScale(scale: Float) {
        v.faceImageViewCamera.apply {
            animate().scaleX(scale).duration = animDuration
            animate().scaleY(scale).duration = animDuration
        }
        v.faceImageViewAnim.apply {
            animate().scaleX(scale).duration = animDuration
            animate().scaleY(scale).duration = animDuration
        }
    }


    /**
     * [Detection.DetectionCallBack] implement
     */
    override fun faceNull() {
        if (hasFace) {
            hasFace = false
            v.faceImageViewAnim.post {
                onViewAnimate {
                    setAlpha(v.faceImageViewAnim.id, 0f)
                }
            }
        }
    }

    override fun hasFace() {
        if (!hasFace) {
            hasFace = true
            v.faceImageViewAnim.post {
                onViewAnimate {
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

    var onFaceEligible: (ByteArray, FacePointData, DataCollect) -> Unit = { _, _, _ -> }

    fun onViewInit() {
        v.faceImageViewAnim.load(R.mipmap.img_progress)
        onLifecycleObserve()
        onStartCamera()
    }
}