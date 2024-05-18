package dev.imanuel.jewels.utils

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.serialization.json.Json

class BarcodeAnalyser(
    val callback: (settings: ServerSettings) -> Unit
) : ImageAnalysis.Analyzer {
    companion object {
        val TAG = BarcodeAnalyser::class.java.simpleName
    }

    private fun checkBarCodes(barcodes: List<Barcode>) {
        if (barcodes.isNotEmpty()) {
            val codeString = barcodes[0].rawValue
            if (codeString != null) {
                try {
                    val settings = Json.decodeFromString<ServerSettings>(codeString)
                    callback(settings)
                } catch (e: Exception) {
                    Log.w(TAG, "analyze: The barcode is invalid", e)
                }
            }
        }
    }

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner = BarcodeScanning.getClient(options)
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { checkBarCodes(it) }
                .addOnFailureListener { Log.w(TAG, "analyze: Failed to process image ${it.message}", it) }
        }
        imageProxy.close()
    }
}
