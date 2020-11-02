package wee.digital.fpa.ui.qr

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.qr.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.util.observerCameraListener
import wee.digital.library.extension.*

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