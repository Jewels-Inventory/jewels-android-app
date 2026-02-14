package dev.imanuel.jewels.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.serialization.json.Json.Default.decodeFromString

@Composable
fun rememberQrScanner(
    onResult: (String) -> Unit,
    onCancel: () -> Unit = {},
    onError: (Exception) -> Unit = {}
): () -> Unit {
    val context = LocalContext.current

    val scanner = remember {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        GmsBarcodeScanning.getClient(context, options)
    }

    return remember(scanner, context) {
        {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val value = barcode.rawValue
                    if (value.isNullOrBlank()) {
                        onError(IllegalStateException("Empty QR value"))
                    } else {
                        onResult(value)
                    }
                }
                .addOnCanceledListener { onCancel() }
                .addOnFailureListener { e -> onError(e) }
        }
    }
}

@Composable
inline fun <reified T> rememberJsonQrScanner(
    crossinline onResult: (T) -> Unit,
    noinline onCancel: () -> Unit = {},
    noinline onError: (Exception) -> Unit = {}
): () -> Unit {
    return rememberQrScanner({ value ->
        val result = decodeFromString<T>(value)
        onResult(result)
    }, onCancel, onError)
}