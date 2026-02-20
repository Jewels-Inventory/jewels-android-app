package dev.imanuel.jewels.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.koin.compose.koinInject

@Composable
fun rememberQrScanner(
    context: Context = koinInject(),
    onResult: (String) -> Unit,
    onCancel: () -> Unit = {},
    onError: (Exception) -> Unit = {}
): () -> Unit {
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
    context: Context = koinInject(),
    crossinline onResult: (T) -> Unit,
    noinline onCancel: () -> Unit = {},
    noinline onError: (Exception) -> Unit = {}
): () -> Unit {
    return rememberQrScanner(context, { value ->
        val result = decodeFromString<T>(value)
        onResult(result)
    }, onCancel, onError)
}