package dev.imanuel.jewels.pages.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import dev.imanuel.jewels.NavigationPage
import dev.imanuel.jewels.R
import dev.imanuel.jewels.pages.HandheldType
import dev.imanuel.jewels.pages.getHandheldType

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            onClick = {
                navController.navigate(NavigationPage.Jewels.name)
            },
            selected = navController.currentDestination?.route == NavigationPage.Jewels.name,
            label = { Text("Jewels") },
            icon = {
                Icon(ImageVector.vectorResource(R.drawable.ic_jewels), "Jewels")
            })
        NavigationBarItem(
            onClick = {
                navController.navigate(NavigationPage.OneTimePasswords.name)
            },
            selected = navController.currentDestination?.route == NavigationPage.OneTimePasswords.name,
            label = { Text("Zwei-Faktor Codes") },
            icon = {
                Icon(ImageVector.vectorResource(R.drawable.ic_otp), "Zwei-Faktor Codes")
            })
    }
}
