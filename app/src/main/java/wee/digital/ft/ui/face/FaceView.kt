package wee.digital.ft.ui.face

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.transition.ChangeBounds
import kotlinx.android.synthetic.main.face.view.*
import wee.digital.ft.R
import wee.digital.ft.app.App
import wee.digital.ft.camera.DataCollect
import wee.digital.ft.camera.Detection
import wee.digital.ft.camera.FacePointData
import wee.digital.ft.camera.RealSenseControl
import wee.digital.ft.util.observerCameraListener
import wee.digital.library.extension.beginTransition
import wee.digital.library.extension.load
import wee.digital.library.extension.reload
import java.util.concurrent.atomic.AtomicInteger


class FaceView : ConstraintLayout,
        RealSenseControl.Listener,
        Detection.DetectionCallBack {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    companion object {
        const val ANIM_DURATION = 400L
    }

    /**
     * [RealSenseControl.Listener] implement
     */
    override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
        colorBitmap ?: return
        dataCollect ?: return
        /*App.recordVideo?.pushFrame(colorBitmap)*/
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
            faceView?.post {
                viewTransition.beginTransition(faceView) {
                    setAlpha(faceImageViewAnim.id, 0f)
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
            faceView.post {
                faceImageViewAnim.reload(R.mipmap.img_progress)
                viewTransition.beginTransition(faceView) {
                    setAlpha(faceImageViewAnim.id, 1f)
                }
            }
        } catch (ignore: Exception) {
        }
    }

    override fun faceEligible(bm: ByteArray, portrait: Bitmap, frameFullFace: ByteArray, faceData: FacePointData, dataCollect: DataCollect) {
        hasFaceReg = false
        hasStream = false
        faceImageViewCamera.setImageBitmap(portrait)
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


    private fun animateImageScale(scale: Float) {
        faceImageViewCamera.apply {
            animate().scaleX(scale).scaleY(scale).duration = ANIM_DURATION
        }
        faceImageViewAnim.apply {
            animate().scaleX(scale).scaleY(scale).duration = ANIM_DURATION
        }
    }

    fun onViewInit(fragment: Fragment) {
        faceImageViewAnim.load(R.mipmap.img_progress)
        fragment.observerCameraListener(this)
        imageLiveData.observe(fragment.viewLifecycleOwner, Observer {
            faceImageViewCamera?.setImageBitmap(it)
        })
        mDetection = Detection(fragment.requireActivity()).also {
            it.initCallBack(this)
        }
    }

    fun animateOnFaceCaptured() {

        val view = faceImageViewCamera
        val viewId = view.id
        val scale = 0.525f

        viewTransition.beginTransition(this, {
            setAlpha(faceTextViewTitle1.id, 0f)
            setAlpha(faceTextViewTitle2.id, 0f)
            setAlpha(faceTextViewTitle3.id, 0f)
            setVerticalBias(viewId, 0.06f)
            connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(viewId, ConstraintSet.BOTTOM, guidelineFace.id, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            animateImageScale(scale)
        }, {
            view.setBackgroundResource(0)
        })
    }

    fun animateOnStartFaceReg() {
        hasStream = true
        val view = faceImageViewCamera
        val viewId = view.id
        val scale = 1f
        viewTransition.beginTransition(this, {
            clear(viewId, ConstraintSet.BOTTOM)
            setVerticalBias(viewId, 0f)
            connect(viewId, ConstraintSet.TOP, faceGuidelineCameraTop.id, ConstraintSet.TOP)
            connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            animateImageScale(scale)
        }, {
            setAlpha(faceTextViewTitle1.id, 1f)
            setAlpha(faceTextViewTitle2.id, 1f)
            setAlpha(faceTextViewTitle3.id, 1f)
        }, {
            view.setBackgroundResource(R.drawable.drw_face)
            view.postDelayed({
                hasFaceReg = true
            }, 600)
        })


    }


}