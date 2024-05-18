package dev.imanuel.jewels.pages

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.imanuel.jewels.utils.BarcodeAnalyser
import dev.imanuel.jewels.utils.saveSettings
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@androidx.camera.core.ExperimentalGetImage
@Composable
fun ServerSetup(goToLogin: () -> Unit) {
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scan den Code aus deinem Browser",
                modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            AndroidView(
                { context ->
                    val cameraExecutor = Executors.newSingleThreadExecutor()
                    val previewView = PreviewView(context).also {
                        it.scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder()
                            .build()
                            .also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                        val imageCapture = ImageCapture.Builder().build()
                        val imageAnalyzer = ImageAnalysis.Builder()
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, BarcodeAnalyser { settings ->
                                    Toast.makeText(context, "Code erkannt", Toast.LENGTH_SHORT).show()
                                    saveSettings(settings, context)
                                    goToLogin()
                                })
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()

                            cameraProvider.bindToLifecycle(
                                context as ComponentActivity, cameraSelector, preview, imageCapture, imageAnalyzer
                            )

                        } catch (exc: Exception) {
                            Log.e("DEBUG", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(context))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp)
        ) {
            Text("Damit du den Code aus deinem Browser scannen kannst, braucht die Jewels App Zugriff auf deine Kamera.")
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text("Kamerazugriff gewähren")
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun ServerSetupreview() {
    ServerSetup { }
}
