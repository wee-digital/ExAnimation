package wee.digital.fpa.ui.screen

import android.graphics.Bitmap
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_qr.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.repository.model.DataCollect
import wee.digital.fpa.ui.base.BaseFragment


class QRFragment : BaseFragment(), ScanQRCode.QRCodeProcessingListener {

    private var mScanQR : ScanQRCode? = null

    private var mCheckQR = false

    override fun layoutResource(): Int = R.layout.fragment_qr

    override fun onViewCreated() {
        mScanQR = ScanQRCode()
        mScanQR?.initListener(this)
        viewOnClick()
        listenerCamera()
    }


    override fun onLiveDataObserve() {

    }

    private fun viewOnClick() {
        frgQRTitle.actionCancelClick { findNavController().popBackStack() }
    }

    /**
     * listener camera
     */
    private fun listenerCamera() {
        App.realSenseControl?.listener = object : RealSenseControl.Listener{

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
        /*if (text.isNotEmpty() && FrameUtil.decryptQRCode(text) != null) {
            DeviceInfoStore.getInstance(activity()).qrCode = text

            findNavController().navigate(R.id.action_scanQRFragment_to_customerFragment)
        } else if (text.isNotEmpty()) {
            show(frgScanQR_error)
        } else {
            gone(frgScanQR_error)
        }*/
        mCheckQR = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        App.realSenseControl?.listener = null
        mScanQR?.destroyScan()
    }

}