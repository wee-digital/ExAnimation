package wee.digital.fpa.ui.screen

import android.graphics.Bitmap
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_qr.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.camera.ScanQRCode
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.model.DeviceInfoStore
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.util.errorQR
import wee.digital.fpa.util.resetErrorQR


class QRFragment : BaseFragment(), ScanQRCode.QRCodeProcessingListener {

    private var mScanQR : ScanQRCode? = null

    private var mCheckQR = false

    private var time = System.currentTimeMillis()

    override fun layoutResource(): Int = R.layout.fragment_qr

    override fun onViewCreated() {
        initView()
        listenerCamera()
    }


    override fun onLiveDataObserve() {}

    private fun initView() {
        App.realSenseControl?.startStreamThread()

        mCheckQR = false
        mScanQR = ScanQRCode()
        mScanQR?.initListener(this)

        Shared.deviceInfo.postValue(DeviceInfoStore(activity()))
        frgQRTitle.actionCancelClick {
            if(System.currentTimeMillis() - time < 1500) return@actionCancelClick
            findNavController().navigate(R.id.action_QRFragment_to_splashFragment)
        }
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
            frgQRStatus.errorQR("Mã không đúng. Bạn vui lòng thử lại lần nữa") { mCheckQR = false }
        } else {
            frgQRStatus.resetErrorQR("Vui lòng đưa mã vào vùng nhận diện") { mCheckQR = false }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        App.realSenseControl?.stopStreamThread()
        App.realSenseControl?.listener = null
    }

}