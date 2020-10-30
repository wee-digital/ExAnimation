package wee.digital.fpa.util

import android.util.Log
import androidx.fragment.app.Fragment
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.RealSenseControl

fun Fragment.observerCameraStream() {
    viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
        override fun onCreated() {
            startCamera()
        }

        override fun onDestroy() {
            stopCamera()
        }
    })
}

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
