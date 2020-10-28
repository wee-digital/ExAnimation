package wee.digital.fpa.ui.qr

import android.graphics.Bitmap
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.device.*
import kotlinx.android.synthetic.main.qr.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.library.extension.*

class QrView(private val v: QrFragment) {

    private var scanQRCode = ScanQRCode()

    private fun onLifecycleObserve() {
        v.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {

            override fun onCreated() {
                onStartCamera()
            }

            override fun onPause() {
                App.realSenseControl?.listener = null
            }

            override fun onDestroy() {
                App.realSenseControl?.stopStreamThread()
            }
        })
    }

    private fun onStartCamera() {
        App.realSenseControl?.listener = object : RealSenseControl.Listener {

            override fun onCameraStarted() {}

            override fun onCameraError(mess: String) {}

            override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
                colorBitmap ?: return
                App.realSenseControl?.hasFace()
                v.requireActivity().runOnUiThread {
                    v.qrImageViewCamera?.setImageBitmap(colorBitmap)
                }
                scanQRCode.decodeQRCode(colorBitmap)
            }
        }
    }

    fun onViewInit() {
        v.addClickListener(v.dialogViewClose)
        onLifecycleObserve()
        App.realSenseControl?.startStreamThread()
        scanQRCode.initListener(v)
    }

    fun onBindMessage(s: String?) {
        if (s.isNullOrEmpty()) {
            v.qrTextViewHint.color(R.color.colorBlack)
            v.qrTextViewHint.text = "Vui lòng đưa mã vào\nvùng nhận diện"
        } else {
            v.qrTextViewHint.paint.shader = LinearGradient(0F, 0F, v.qrTextViewHint.width.toFloat(), 0F,
                    ContextCompat.getColor(v.requireContext(), R.color.gradient_red_start),
                    ContextCompat.getColor(v.requireContext(), R.color.gradient_red_end),
                    Shader.TileMode.CLAMP)
            v.qrTextViewHint.text = s
        }
    }

    fun onProgressChanged(isVisibility: Boolean) {
        if (isVisibility) {
            v.qrTextViewHint.gone()
            v.qrViewProgress.load(R.mipmap.img_progress)
        } else {
            v.qrTextViewHint.show()
            v.qrViewProgress.clear()
        }
    }
}