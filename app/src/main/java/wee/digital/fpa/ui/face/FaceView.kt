package wee.digital.fpa.ui.face

import android.graphics.Bitmap
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.face.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.Detection
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.fpa.util.SimpleTransitionListener
import wee.digital.library.extension.load


class FaceView(private val v: FaceFragment) : Detection.DetectionCallBack {

    private var mDetection: Detection? = null

    var hasFaceDetect = true

    private val viewTransition = ChangeBounds().apply {
        duration = 400
    }

    private fun onLifecycleObserve() {
        v.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onDestroy() {
                App.realSenseControl?.listener = null
                App.realSenseControl?.stopStreamThread()
            }
        })
    }

    private fun onStartCamera() {
        RsContext.init(v.requireContext())
        UsbUtilities.grantUsbPermissionIfNeeded(v.requireContext())
        App.realSenseControl?.startStreamThread()
        mDetection = Detection(v.requireActivity())
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

    fun animateOnFaceCaptured() {
        val viewId = v.faceImageViewCamera.id
        val height = (v.faceImageViewCamera.width / 2.23).toInt()
        viewTransition.addListener(object : SimpleTransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                viewTransition.removeListener(this)
                viewTransition.duration = 600
                onViewAnimate {
                    setAlpha(v.faceImageViewAnim.id, 1f)
                }
            }
        })
        viewTransition.duration = 400
        onViewAnimate {
            setAlpha(v.faceTextViewTitle1.id, 0f)
            setAlpha(v.faceTextViewTitle2.id, 0f)
            setAlpha(v.faceTextViewTitle3.id, 0f)
            connect(viewId, ConstraintSet.TOP, v.faceTextViewRemaining.id, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.BOTTOM, v.guidelineFace.id, ConstraintSet.BOTTOM)
            constrainHeight(viewId, height)
            constrainDefaultHeight(viewId, height)
        }
    }

    fun animateOnStartFaceReg(onEnd: () -> Unit) {
        val viewId = v.faceImageViewCamera.id
        val height = (v.faceImageViewCamera.width * 2.23).toInt()
        viewTransition.addListener(object : SimpleTransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                viewTransition.removeListener(this)
                onEnd()
            }
        })
        viewTransition.duration = 400
        onViewAnimate {
            setAlpha(v.faceImageViewAnim.id, 0f)
            setAlpha(v.faceTextViewTitle1.id, 1f)
            setAlpha(v.faceTextViewTitle2.id, 1f)
            setAlpha(v.faceTextViewTitle3.id, 1f)
            clear(viewId, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.TOP, v.faceGuidelineCameraTop.id, ConstraintSet.TOP)
            constrainHeight(viewId, height)
            constrainDefaultHeight(viewId, height)
        }
    }

    private fun onViewAnimate(block: ConstraintSet.() -> Unit) {
        TransitionManager.beginDelayedTransition(v.viewContent, viewTransition)
        ConstraintSet().also {
            it.clone(v.viewContent)
            it.block()
            it.applyTo(v.viewContent)
        }
    }

    /**
     * [Detection.DetectionCallBack] implement
     */
    override fun faceNull() {}

    override fun hasFace() {}

    override fun faceEligible(bm: ByteArray, frameFullFace: ByteArray, faceData: FacePointData, dataCollect: DataCollect) {
        hasFaceDetect = false
        onFaceEligible(bm, faceData, dataCollect)
    }

    var onFaceEligible: (ByteArray, FacePointData, DataCollect) -> Unit = { _, _, _ -> }

    fun onViewInit() {
        v.faceImageViewAnim.load(R.mipmap.img_progress)
        onLifecycleObserve()
        onStartCamera()
    }
}