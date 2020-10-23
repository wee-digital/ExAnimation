package wee.digital.fpa.ui.screen

import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_qr.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.ui.base.BaseFragment


class QRFragment : BaseFragment(), ScanQRCode.QRCodeProcessingListener {

    private var mScanQR : ScanQRCode? = null

    private var mCheckQR = false

    override fun layoutResource(): Int = R.layout.fragment_qr

    override fun onViewCreated() {
        mCheckQR = false
        mScanQR = ScanQRCode()
        mScanQR?.initListener(this)
        initView()
        listenerCamera()
    }


    override fun onLiveDataObserve() {}

    private fun initView() {
        Shared.deviceInfo.postValue(DeviceInfo())
        frgQRTitle.actionCancelClick { findNavController().popBackStack() }
    }

    /**
     * listener camera
     */
    private fun listenerCamera() {
        App.realSenseControl?.listener = object : RealSenseControl.Listener {

            override fun onCameraStarted() {}

            override fun onCameraError(mess: String) {}

            override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
                colorBitmap ?: return

                App.realSenseControl?.hasFace()

                activity?.runOnUiThread { frgQRFrame?.setImageBitmap(colorBitmap) }

                mScanQR?.decodeQRCode(colorBitmap)
            }
        }
    }

    /**
     * listener scanQR Code
     */
    override fun onResult(result: String) {
        if(mCheckQR) return
        mCheckQR = true
        checkQRCode(result)
    }

    private fun checkQRCode(text: String) {
        if (text.isNotEmpty() && FrameUtil.decryptQRCode(text) != null) {
            Shared.deviceInfo.value?.qrCode = text
            findNavController().navigate(R.id.action_QRFragment_to_InfoDeviceFragment)
        } else if (text.isNotEmpty()) {
            frgQRStatus.error("Mã không đúng. Bạn vui lòng thử lại lần nữa")
        } else {
            frgQRStatus.resetError("Vui lòng đưa mã vào vùng nhận diện")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mScanQR?.destroyScan()
        mScanQR = null
        App.realSenseControl?.listener = null

    }

    private fun AppCompatTextView.error(value: String) {
        mCheckQR = false
        this.text = value
        this.setTextColor(ContextCompat.getColor(context, R.color.colorAlert))
    }

    private fun AppCompatTextView.resetError(value: String) {
        mCheckQR = false
        this.text = value
        this.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
    }

}