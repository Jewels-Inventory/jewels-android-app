package dev.imanuel.jewels.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
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
import androidx.glance.text.FontFamily
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.imanuel.jewels.detection.api.SimpleOneTimePassword
import dev.imanuel.jewels.detection.api.getOneTimePasswords
import dev.imanuel.jewels.pages.generateOtp
import io.ktor.client.HttpClient
import org.koin.core.context.GlobalContext

class RefreshOtpAction : ActionCallback {
    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        OneTimePasswordWidget().update(context, glanceId)
    }
}

class OneTimePasswordWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val res = getOneTimePasswords(
            GlobalContext.get().get<HttpClient>(), context
        )

        val passwords = (res.myOneTimePasswords.map {
            SimpleOneTimePassword.fromOneTimePassword(it)
        } + res.sharedOneTimePasswords.map {
            SimpleOneTimePassword.fromSharedOneTimePassword(it)
        }).sortedBy { it.accountIssuer + it.accountName }

        provideContent {
            CodesUi(passwords)
        }
    }

    @Composable
    private fun CodesUi(otps: List<SimpleOneTimePassword>) {
        GlanceTheme {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.Start
            ) {
                LazyColumn(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    items(
                        otps,
                        itemId = { otp -> otp.id }
                    ) { otp ->
                        val totp = remember(otp.id) {
                            generateOtp(otp.secretKey)
                        }

                        Column(
                            modifier = GlanceModifier
                                .padding(4.dp)
                                .clickable(
                                    actionRunCallback<RefreshOtpAction>()
                                )
                        ) {
                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .background(GlanceTheme.colors.inversePrimary)
                                    .cornerRadius(18.dp)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = GlanceModifier.defaultWeight()
                                ) {
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
                                    text = totp,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        color = GlanceTheme.colors.onSurface,
                                        fontFamily = FontFamily.Monospace
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
