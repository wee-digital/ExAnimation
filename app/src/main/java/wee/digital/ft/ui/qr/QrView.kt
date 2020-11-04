package wee.digital.ft.ui.qr

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.qr.*
import wee.digital.ft.R
import wee.digital.ft.app.App
import wee.digital.ft.camera.DataCollect
import wee.digital.ft.camera.RealSenseControl
import wee.digital.ft.camera.ScanQRCode
import wee.digital.ft.util.observerCameraListener
import wee.digital.library.extension.gradientHorizontal
import wee.digital.library.extension.hide
import wee.digital.library.extension.load
import wee.digital.library.extension.show

class QrView(private val v: QrFragment) : RealSenseControl.Listener {


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

    fun onViewInit() {
        v.qrViewProgress.load(R.mipmap.img_progress)
        v.addClickListener(v.dialogViewClose)
        v.observerCameraListener(this)
        imageLiveData.observe(v.viewLifecycleOwner, Observer {
            v.qrImageViewCamera?.setImageBitmap(it)
        })
        scanQRCode.initListener(v)
    }

    fun onBindMessage(s: String?) {
        if (s.isNullOrEmpty()) {
            v.qrTextViewHint.gradientHorizontal(R.color.colorTextPrimary)
            v.qrTextViewHint.text = "Vui lòng đưa mã vào\nvùng nhận diện"
        } else {
            v.qrTextViewHint.gradientHorizontal(R.color.colorAlertStart, R.color.colorAlertEnd)
            v.qrTextViewHint.text = s
        }
    }

    fun onBindProgress(isShow: Boolean) {
        v.view?.post {
            if (isShow) {
                v.qrTextViewHint?.hide()
                v.qrViewProgress?.show()
            } else {
                v.qrTextViewHint?.show()
                v.qrViewProgress?.hide()
            }
        }
    }

}