package wee.digital.fpa.ui.face

import android.graphics.Bitmap
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
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
import wee.digital.library.extension.clear
import wee.digital.library.extension.load


class FaceView(private val v: FaceFragment) : Detection.DetectionCallBack {

    private var mDetection: Detection? = null

    var hasFaceDetect = false

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
                v.requireActivity().runOnUiThread {
                    v.faceImageViewCamera?.setImageBitmap(colorBitmap)
                }
                if (hasFaceDetect) return
                mDetection?.bitmapChecking(colorBitmap, depthBitmap, dataCollect)
            }

        }
    }

    private fun animateOnFaceCapture() {
        /*v.bgFaceLine.numberLine = 0f
        v.bgFaceRounded.number = 0f*/
        v.faceImageViewAnim.load(R.mipmap.img_progress)
        val viewId = v.faceImageViewCamera.id
        onViewAnimate {
            setVisibility(v.faceTitleGroup.id, View.INVISIBLE)
            connect(viewId, ConstraintSet.TOP, v.faceImageViewCamera.id, ConstraintSet.TOP)
            connect(viewId, ConstraintSet.BOTTOM, v.faceGuidelineCameraTop.id, ConstraintSet.BOTTOM)
            constrainHeight(viewId, v.faceImageViewCamera.width / 2)
            constrainDefaultHeight(viewId, v.faceImageViewCamera.width / 2)
        }

    }

    fun animateOnFaceDetect() {
        /* v.bgFaceLine.numberLine = 1f
         v.bgFaceRounded.number = 1f*/
        v.faceImageViewAnim.clear()
        val viewId = v.faceImageViewCamera.id
        onViewAnimate {
            setVisibility(v.faceTitleGroup.id, View.VISIBLE)
            clear(viewId, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.TOP, v.faceGuidelineCameraTop.id, ConstraintSet.TOP)
            connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constrainHeight(viewId, v.faceImageViewCamera.width * 2)
            constrainDefaultHeight(viewId, v.faceImageViewCamera.width * 2)
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
        if (!hasFaceDetect) return
        hasFaceDetect = false
        animateOnFaceCapture()
        onFaceEligible(bm, faceData, dataCollect)
    }

    var onFaceEligible: (ByteArray, FacePointData, DataCollect) -> Unit = { _, _, _ -> }

    fun onViewInit() {
        onLifecycleObserve()
        onStartCamera()
    }
}