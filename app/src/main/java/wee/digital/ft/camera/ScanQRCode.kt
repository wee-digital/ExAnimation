package wee.digital.ft.camera

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanQRCode {

    fun initListener(l: QRCodeProcessingListener) {
        scanListener = l
    }

    private var scanListener: QRCodeProcessingListener? = null

    private var isProcessing = false

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun decodeQRCode(bitmap: Bitmap?) {
        bitmap ?: return
        if (isProcessing) return
        isProcessing = true
        decodeQRCodeAsync(bitmap).addOnSuccessListener {
            scanListener?.onResult(it)
            isProcessing = false
        }.addOnFailureListener {
            isProcessing = false
        }
    }

    fun destroyScan() {
        executorService.shutdown()
    }


    private fun decodeQRCodeAsync(bitmap: Bitmap): Task<String> {
        return Tasks.call(executorService, { decode(bitmap) })
    }

    private fun decode(bitmapIn: Bitmap?): String {
        var textResult = ""
        if (bitmapIn == null) {
            return textResult
        }
        val bmp = bitmapIn.copy(Bitmap.Config.ARGB_8888, true)
        val intArray = IntArray(bmp.width * bmp.height)
        bmp.getPixels(
                intArray, 0, bmp.width, 0, 0, bmp.width,
                bmp.height
        )
        val source: LuminanceSource = RGBLuminanceSource(
                bmp.width, bmp.height, intArray
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            textResult = result.text
        } catch (e: Exception) {
            return ""
        }
        return textResult
    }

    interface QRCodeProcessingListener {
        fun onResult(result: String)
    }
}