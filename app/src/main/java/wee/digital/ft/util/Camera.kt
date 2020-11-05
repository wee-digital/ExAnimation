package wee.digital.ft.util

import android.util.Log
import androidx.fragment.app.Fragment
import wee.digital.ft.app.App
import wee.digital.ft.camera.RealSenseControl


fun Fragment.observerCameraListener(listener: RealSenseControl.Listener) {
    viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
        override fun onCreated() {
            App.realSenseControl?.listener = listener
        }

        override fun onPause() {
            Log.d("FaceView", "App.realSenseControl?.listener = null")
            App.realSenseControl?.listener = null
        }

    })
}

fun startCamera() {
    App.realSenseControl?.startStreamThread()
}

fun stopCamera() {
    App.realSenseControl?.stopStreamThread()
}
