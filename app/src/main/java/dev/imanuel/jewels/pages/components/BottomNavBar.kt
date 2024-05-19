package dev.imanuel.jewels.pages.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import dev.imanuel.jewels.NavigationPage
import dev.imanuel.jewels.R
import dev.imanuel.jewels.pages.HandheldType
import dev.imanuel.jewels.pages.getHandheldType

@Composable
fun BottomNavBar(hasWatch: Boolean, navController: NavController) {
    val handheldType = getHandheldType()

    NavigationBar {
        NavigationBarItem(
            onClick = {
                navController.navigate("jewels")
            },
            selected = navController.currentDestination?.route == "jewels",
            label = { Text("Jewels") },
            icon = {
                Icon(ImageVector.vectorResource(R.drawable.ic_jewels), "Jewels")
            })
        NavigationBarItem(
            onClick = {
                navController.navigate("handheld")
            },
            selected = navController.currentDestination?.route == "handheld",
            label = { Text(handheldType.toString()) },
            icon = {
                Icon(
                    ImageVector.vectorResource(
                        if (handheldType == HandheldType.Tablet) {
                            R.drawable.ic_tablet
                        } else {
                            R.drawable.ic_phone
                        }
                    ), getHandheldType().toString()
                )
            })
        if (hasWatch) {
            NavigationBarItem(
                onClick = {
                    navController.navigate("watch")
                },
                selected = navController.currentDestination?.route == "watch",
                label = { Text("Smartwatch") },
                icon = {
                    Icon(ImageVector.vectorResource(R.drawable.ic_watch), "Smartwatch")
                })
        }
    }
}
