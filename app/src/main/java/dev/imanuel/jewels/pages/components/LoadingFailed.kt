package dev.imanuel.jewels.pages.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import dev.imanuel.jewels.R

@Composable
fun LoadingFailed() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Laden fehlgeschlagen",
            style = MaterialTheme.typography.headlineLarge
        )
        Image(
            ImageBitmap.imageResource(R.drawable.loading_error),
            contentDescription = "Laden fehlgeschlagen",
            modifier = Modifier.fillMaxWidth()
        )
    }
}