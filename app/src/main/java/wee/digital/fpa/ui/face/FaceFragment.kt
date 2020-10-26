package wee.digital.fpa.ui.face

import android.graphics.Bitmap
import android.util.Base64
import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.fragment_face.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.Detection
import wee.digital.fpa.camera.FacePointData
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.dto.VerifyFaceDTOReq
import wee.digital.fpa.ui.base.BaseFragment

class FaceFragment : BaseFragment(), Detection.DetectionCallBack {

    private val vm: FaceVM by lazy { FaceVM() }

    private var mDetection: Detection? = null

    private var mIsDetectionFace = false

    override fun layoutResource(): Int = R.layout.fragment_face

    override fun onViewCreated() {
        RsContext.init(context)
        UsbUtilities.grantUsbPermissionIfNeeded(context)
        App.realSenseControl?.startStreamThread()
    }

    override fun onLiveDataObserve() {}

    private fun initListenerCamera() {
        mDetection = Detection(activity())

        App.realSenseControl?.listener = object : RealSenseControl.Listener {

            override fun onCameraStarted() {}

            override fun onCameraError(mess: String) {}

            override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
                colorBitmap ?: return
                dataCollect ?: return
                activity?.runOnUiThread { frgFaceFrame?.setImageBitmap(colorBitmap) }
                if (mIsDetectionFace) return
                mDetection?.bitmapChecking(colorBitmap, depthBitmap, dataCollect)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        initListenerCamera()
    }

    override fun onPause() {
        super.onPause()
        App.realSenseControl?.listener = null
    }

    /**
     * implement detectionFace
     */
    override fun faceNull() {}

    override fun hasFace() {}

    override fun faceEligible(bm: ByteArray, frameFullFace: ByteArray, faceData: FacePointData, dataCollect: DataCollect) {
        if (mIsDetectionFace) return
        mIsDetectionFace = true

        frgFaceBackground?.showFrameResult(frameFullFace)

        val face = Base64.encodeToString(bm, Base64.NO_WRAP)
        val req = VerifyFaceDTOReq(face, Shared.paymentID.value ?: "", Shared.clientID.value ?: "")
        vm.verifyFace(req, faceData, dataCollect)
    }

}