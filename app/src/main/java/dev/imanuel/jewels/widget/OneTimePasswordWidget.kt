package dev.imanuel.jewels.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
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
import dev.imanuel.jewels.pages.CurrentState
import dev.imanuel.jewels.pages.generateOtp
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

@Composable
fun rememberTotpState(
    secretKey: String, periodSeconds: Int = 30
): State<CurrentState> {
    val state = remember { mutableStateOf(CurrentState("", periodSeconds)) }

    LaunchedEffect(secretKey, periodSeconds) {
        var lastStep: Long = -1L

        while (isActive) {
            val nowSec = System.currentTimeMillis() / 1000L
            val step = nowSec / periodSeconds
            val secondsRemaining = (periodSeconds - (nowSec % periodSeconds)).toInt()

            if (step != lastStep) {
                lastStep = step
                val newCode = generateOtp(secretKey)
                state.value = CurrentState(newCode, secondsRemaining)
            } else {
                state.value = CurrentState(state.value.code, secondsRemaining)
            }

            delay(1_000)
        }
    }

    return state
}

class OneTimePasswordWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val httpClient = GlobalContext.get().get<HttpClient>()

            CodesUi(httpClient)
        }
    }

    @Composable
    private fun CodesUi(httpClient: HttpClient) {
        var otps by remember { mutableStateOf<List<SimpleOneTimePassword>>(emptyList()) }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        val loadOtps = {
            coroutineScope.launch {
                val res = getOneTimePasswords(httpClient, context)
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
                LazyColumn(
                    modifier = GlanceModifier
                        .fillMaxSize(),
                ) {
                    items(otps) { otp ->
                        val totp = rememberTotpState(otp.secretKey)
                        Column(
                            modifier = GlanceModifier
                                .padding(4.dp)
                        ) {
                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .background(GlanceTheme.colors.inversePrimary)
                                    .cornerRadius(18.dp)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = GlanceModifier.defaultWeight()) {
                                    Text(
                                        text = otp.accountIssuer,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = GlanceTheme.colors.onSurface
                                        )
                                    )
                                    Text(
                                        text = otp.accountName,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = GlanceTheme.colors.onSurface
                                        )
                                    )
                                }
                                Text(
                                    text = totp.value.code,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        color = GlanceTheme.colors.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}