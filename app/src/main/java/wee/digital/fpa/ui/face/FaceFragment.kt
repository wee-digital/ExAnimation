package wee.digital.fpa.ui.face

import android.graphics.Bitmap
import kotlinx.android.synthetic.main.fragment_face.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.DataCollect
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.ui.base.BaseFragment

class FaceFragment : BaseFragment() {

    private var mFrame : Bitmap? = null

    override fun layoutResource(): Int = R.layout.fragment_face

    override fun onViewCreated() {
        button.setOnClickListener { frgFaceBackground?.showImageResult(mFrame) }
        button2.setOnClickListener { frgFaceBackground?.hideImageResult() }
    }

    override fun onLiveDataObserve() {}

    private fun initListenerCamera(){
        App.realSenseControl?.listener = object : RealSenseControl.Listener{
            override fun onCameraStarted() {}
            override fun onCameraError(mess: String) {}
            override fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?) {
                colorBitmap ?: return
                mFrame = colorBitmap
                activity?.runOnUiThread {
                    frgFaceFrame?.setImageBitmap(colorBitmap)
                }
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

}