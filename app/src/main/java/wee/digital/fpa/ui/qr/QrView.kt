package wee.digital.fpa.ui.qr

import android.graphics.Bitmap
import kotlinx.android.synthetic.main.qr.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.library.extension.color

class QrView(private val v: QrFragment) {

    private var scanQRCode = ScanQRCode()

    private fun onLifecycleObserve() {
        v.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {

            override fun onResume() {
                super.onResume()
                onStartCamera()
            }

            override fun onPause() {
                super.onPause()
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
        onLifecycleObserve()
        App.realSenseControl?.startStreamThread()
        scanQRCode.initListener(v)

    }

    fun onBindMessage(s: String?) {
        if (s.isNullOrEmpty()) {
            v.qrTextViewHint.color(R.color.colorBlack)
            v.qrTextViewHint.text = "Vui lòng đưa mã vào vùng nhận diện"
        } else {
            v.qrTextViewHint.color(R.color.colorAlert)
            v.qrTextViewHint.text = s
        }
    }


}