package wee.digital.camera.detector

import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions

class ModelFilter(fileName: String) {

    private var imageLabeler: FirebaseVisionImageLabeler? = null

    private var isChecking: Boolean = false

    init {
        try {
            val model = FirebaseAutoMLLocalModel.Builder()
                    .setAssetFilePath(fileName)
                    .build()
            val options =
                    FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(model)
                            .setConfidenceThreshold(0.5f)
                            .build()
            imageLabeler = FirebaseVision.getInstance()
                    .getOnDeviceAutoMLImageLabeler(options)
        } catch (e: Exception) {
        }
    }

    @Synchronized
    fun processImage(bitmap: Bitmap, onResult: (String?, Float) -> Unit) {
        if (isChecking) {
            return
        }
        isChecking = true
        try {
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            imageLabeler?.processImage(image)
                    ?.addOnSuccessListener {
                        val label = it.firstOrNull()
                        onResult(label?.text, label?.confidence ?: 100f)
                        isChecking = false
                    }
                    ?.addOnFailureListener {
                        isChecking = false
                    }

        } catch (e: Exception) {
            isChecking = false
        }
    }

    fun destroy() {
        imageLabeler?.close()
    }

}
