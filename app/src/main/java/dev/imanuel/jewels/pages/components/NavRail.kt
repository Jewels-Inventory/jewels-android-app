package dev.imanuel.jewels.pages.components

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.glance.LocalContext
import androidx.navigation.NavController
import com.google.android.gms.common.util.DeviceProperties.isTablet
import dev.imanuel.jewels.NavigationPage
import dev.imanuel.jewels.R
import dev.imanuel.jewels.pages.getHandheldType
import org.koin.compose.koinInject


@Composable
fun NavRail(
    navController: NavController,
    context: Context = koinInject(),
    content: @Composable () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        if (isTablet(context)) {
            NavigationRail(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically),
            ) {
                Spacer(Modifier.weight(1f))
                NavigationRailItem(
                    onClick = {
                        navController.navigate(NavigationPage.Jewels.name)
                    },
                    selected = navController.currentDestination?.route == NavigationPage.Jewels.name,
                    label = { Text("Jewels") },
                    icon = {
                        Icon(ImageVector.vectorResource(R.drawable.ic_jewels), "Jewels")
                    })
                NavigationRailItem(
                    onClick = {
                        navController.navigate(NavigationPage.OneTimePasswords.name)
                    },
                    selected = navController.currentDestination?.route == NavigationPage.OneTimePasswords.name,
                    label = { Text("Zwei-Faktor Codes") },
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_otp),
                            "Zwei-Faktor Codes"
                        )
                    })
                Spacer(Modifier.weight(1f))
            }
        }

        content()
    }
}