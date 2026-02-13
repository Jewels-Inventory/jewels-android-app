package dev.imanuel.jewels.widget

import android.app.KeyguardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.imanuel.jewels.api.SimpleOneTimePassword
import dev.imanuel.jewels.api.getOneTimePasswords
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class OneTimePasswordWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val km = context.getSystemService(KeyguardManager::class.java)
            val locked = km.isKeyguardLocked || km.isDeviceLocked

            val httpClient = GlobalContext.get().get<HttpClient>()

            CodesUi(httpClient)
        }
    }

    @Composable
    private fun CodesUi(httpClient: HttpClient) {
        var otps by remember { mutableStateOf<List<SimpleOneTimePassword>>(emptyList()) }
        val coroutineScope = rememberCoroutineScope()

        val loadOtps = {
            coroutineScope.launch {
                val res = getOneTimePasswords(httpClient)
                val passwords =
                    (res.myOneTimePasswords.map { SimpleOneTimePassword.fromOneTimePassword(it) } + res.sharedOneTimePasswords.map {
                        SimpleOneTimePassword.fromSharedOneTimePassword(it)
                    }).sortedBy { it.accountIssuer + it.accountName }
                otps = passwords
            }
        }

        LaunchedEffect(Unit) {
            loadOtps()
        }

        GlanceTheme {
            Column(
                modifier = GlanceModifier.fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.Start
            ) {
                LazyColumn {
                    items(otps) { otp ->
                        var code by remember { mutableStateOf(GoogleAuthenticator(otp.secretKey.toByteArray()).generate()) }

                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable(block = {
                                    code =
                                        GoogleAuthenticator(otp.secretKey.toByteArray()).generate()
                                }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = GlanceModifier.defaultWeight()) {
                                Text(otp.accountIssuer, style = TextStyle(fontSize = 16.sp))
                                Text(otp.accountName, style = TextStyle(fontSize = 12.sp))
                            }
                            Text(code, style = TextStyle(fontSize = 20.sp))
                        }
                    }
                }
            }
        }
    }
}