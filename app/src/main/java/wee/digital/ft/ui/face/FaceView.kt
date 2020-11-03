package wee.digital.ft.ui.face

import android.graphics.Bitmap
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.transition.ChangeBounds
import kotlinx.android.synthetic.main.face.*
import wee.digital.ft.R
import wee.digital.ft.app.App
import wee.digital.ft.camera.DataCollect
import wee.digital.ft.camera.Detection
import wee.digital.ft.camera.FacePointData
import wee.digital.ft.camera.RealSenseControl
import wee.digital.ft.util.observerCameraListener
import wee.digital.library.extension.beginTransition
import wee.digital.library.extension.load
import wee.digital.library.extension.loadGif
import wee.digital.library.extension.onEndTransition
import java.util.concurrent.atomic.AtomicInteger


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
        if (hasStream) {
            imageLiveData.postValue(colorBitmap)
        }
        if (hasFaceReg) {
            mDetection?.bitmapChecking(colorBitmap, depthBitmap, dataCollect)
        }
    }

    private val nonFaceCount = AtomicInteger(40)

    /**
     * [Detection.DetectionCallBack] implement
     */
    override fun faceNull() {
        App.realSenseControl?.hasFace()
        if (!hasFace && nonFaceCount.decrementAndGet() > 0) return
        hasFace = false
        try {
            v.faceImageViewAnim.post {
                viewTransition.beginTransition(v.viewContent) {
                    setAlpha(v.faceImageViewAnim.id, 0f)
                }
            }
        } catch (ignore: Exception) {
        }
    }

    override fun hasFace() {
        App.realSenseControl?.hasFace()
        nonFaceCount.set(20)
        if (hasFace) return
        hasFace = true
        try {
            v.faceImageViewAnim.post {
                v.faceImageViewAnim.loadGif(R.mipmap.img_progress)
                viewTransition.beginTransition(v.viewContent) {
                    setAlpha(v.faceImageViewAnim.id, 1f)
                }
            }
        } catch (ignore: Exception) {
        }
    }

    override fun faceEligible(bm: ByteArray, portrait: Bitmap, frameFullFace: ByteArray, faceData: FacePointData, dataCollect: DataCollect) {
        hasFaceReg = false
        hasStream = false
        v.faceImageViewCamera.setImageBitmap(portrait)
        onFaceEligible(bm, faceData, dataCollect)
    }

    /**
     * [FaceView] properties
     */
    private var mDetection: Detection? = null

    private var hasStream = true

    private var hasFaceReg = true

    var onFaceEligible: (ByteArray, FacePointData, DataCollect) -> Unit = { _, _, _ -> }

    private var hasFace: Boolean = false

    private val viewTransition = ChangeBounds().apply {
        duration = ANIM_DURATION
    }

    private val imageLiveData = MutableLiveData<Bitmap>()

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
        imageLiveData.observe(v.viewLifecycleOwner, Observer {
            v.faceImageViewCamera?.setImageBitmap(it)
        })
        onConfigFaceReg()
    }

    fun animateOnFaceCaptured() {

        val view = v.faceImageViewCamera
        val viewId = view.id
        val scale = 0.525f

        viewTransition.onEndTransition {
            view.setBackgroundResource(0)
        }
        viewTransition.beginTransition(v.viewContent) {
            setAlpha(v.faceTextViewTitle1.id, 0f)
            setAlpha(v.faceTextViewTitle2.id, 0f)
            setAlpha(v.faceTextViewTitle3.id, 0f)
            setVerticalBias(viewId, 0.06f)
            connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(viewId, ConstraintSet.BOTTOM, v.guidelineFace.id, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        }
        animateImageScale(scale)

    }

    fun animateOnStartFaceReg() {
        hasStream = true
        val view = v.faceImageViewCamera
        val viewId = view.id
        val scale = 1f
        viewTransition.onEndTransition {
            view.setBackgroundResource(R.drawable.drw_face)
            view.postDelayed({
                hasFaceReg = true
            }, 600)
        }
        viewTransition.beginTransition(v.viewContent, {
            clear(viewId, ConstraintSet.BOTTOM)
            setVerticalBias(viewId, 0f)
            connect(viewId, ConstraintSet.TOP, v.faceGuidelineCameraTop.id, ConstraintSet.TOP)
            connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        }, {
            setAlpha(v.faceTextViewTitle1.id, 1f)
            setAlpha(v.faceTextViewTitle2.id, 1f)
            setAlpha(v.faceTextViewTitle3.id, 1f)
        })
        animateImageScale(scale)

    }


}