@file:OptIn(ExperimentalMaterial3Api::class)

package dev.imanuel.jewels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.imanuel.jewels.pages.Information
import dev.imanuel.jewels.pages.Login
import dev.imanuel.jewels.pages.ServerSetup
import dev.imanuel.jewels.ui.theme.JewelsTheme
import dev.imanuel.jewels.utils.loadSettings

class MainActivity : ComponentActivity() {
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainComposable()
        }
    }
}

@Composable
@ExperimentalGetImage
fun MainComposable() {
    val navController = rememberNavController()
    val settings = loadSettings(LocalContext.current)

    JewelsTheme {
        NavHost(
            navController = navController,
            startDestination = if (settings != null) {
                "information"
            } else {
                "serverSetup"
            }
        ) {
            composable("information") { Information(goToSetup = { navController.navigate("serverSetup") }) }
            composable("serverSetup") { ServerSetup(goToLogin = { navController.navigate("login") }) }
            composable("login") {
                Login(
                    goToInformation = { navController.navigate("information") },
                    goToSetup = { navController.navigate("serverSetup") })
            }
        }
    }
}