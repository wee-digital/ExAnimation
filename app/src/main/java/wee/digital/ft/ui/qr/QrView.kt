package wee.digital.ft.ui.qr

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.transition.ChangeBounds
import kotlinx.android.synthetic.main.qr.view.*
import wee.digital.ft.R
import wee.digital.ft.app.App
import wee.digital.ft.camera.DataCollect
import wee.digital.ft.camera.RealSenseControl
import wee.digital.ft.camera.ScanQRCode
import wee.digital.ft.util.observerCameraListener
import wee.digital.library.extension.*

class QrView : ConstraintLayout, RealSenseControl.Listener {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * [RealSenseControl.Listener] implement
     */
    override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
        colorBitmap ?: return
        App.realSenseControl?.hasFace()
        imageLiveData.postValue(colorBitmap)
        scanQRCode.decodeQRCode(colorBitmap)
    }

    /**
     * [QrView] properties
     */
    private var scanQRCode = ScanQRCode()

    private val imageLiveData = MutableLiveData<Bitmap>()

    private val viewTransition = ChangeBounds().apply {
        duration = 1600
    }

    fun onViewInit(fragment: Fragment, qrListener: ScanQRCode.QRCodeProcessingListener) {
        qrViewProgress.load(R.mipmap.img_progress)
        animateUp()
        fragment.observerCameraListener(this)
        imageLiveData.observe(fragment.viewLifecycleOwner, Observer {
            qrImageViewCamera?.setImageBitmap(it)
        })
        scanQRCode.initListener(qrListener)
    }

    fun onBindMessage(s: String?) {
        if (s.isNullOrEmpty()) {
            qrTextViewHint.gradientHorizontal(R.color.colorTextPrimary)
            qrTextViewHint.text = "Vui lòng đưa mã vào\nvùng nhận diện"
        } else {
            qrTextViewHint.gradientHorizontal(R.color.colorAlertStart, R.color.colorAlertEnd)
            qrTextViewHint.text = s
        }
    }

    fun onBindProgress(isShow: Boolean) {
        post {
            if (isShow) {
                qrTextViewHint?.hide()
                qrViewProgress?.show()
            } else {
                qrTextViewHint?.show()
                qrViewProgress?.hide()
            }
        }
    }

    private fun animateUp() {
        val viewId = qrViewScanAnim.id
        viewTransition.beginTransition(qrLayoutAnim, {
            clear(viewId, ConstraintSet.TOP)
            connect(viewId, ConstraintSet.BOTTOM, qrLayoutAnim.id, ConstraintSet.TOP)
        }, {
            setRotationX(viewId, 180f)
            animateDown()
        })
    }

    private fun animateDown() {
        val viewId = qrViewScanAnim.id
        viewTransition.beginTransition(qrLayoutAnim, {
            clear(viewId, ConstraintSet.BOTTOM)
            connect(viewId, ConstraintSet.TOP, qrLayoutAnim.id, ConstraintSet.BOTTOM)
        }, {
            setRotationX(viewId, 0f)
            animateUp()
        })
    }

}