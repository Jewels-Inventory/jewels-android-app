package dev.imanuel.jewels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AnimatedPage
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.PagerScaffoldDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.VerticalPagerScaffold
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import dev.imanuel.jewels.detection.api.SimpleOneTimePassword
import dev.imanuel.jewels.detection.api.getOneTimePasswords
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds

fun generateOtp(secretKey: String): String {
    return GoogleAuthenticator(secretKey.encodeToByteArray()).generate()
}

data class CurrentState(val code: String, val secondsRemaining: Int)

@Composable
fun rememberTotpState(
    secretKey: String,
    periodSeconds: Int = 30
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

            delay(1.seconds)
        }
    }

    return state
}

@Composable
fun OtpItem(
    otp: SimpleOneTimePassword,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
    shape: Shape = RoundedCornerShape(18.dp),
) {
    val backgroundPainter = ColorPainter(MaterialTheme.colorScheme.surfaceContainer)
    val finalPainter = if (transformation != null) {
        remember(transformation, backgroundPainter, shape) {
            transformation.createContainerPainter(backgroundPainter, shape, border = null)
        }
    } else {
        backgroundPainter
    }

    val totp by rememberTotpState(otp.secretKey, periodSeconds = 30)

    val targetProgress by remember {
        derivedStateOf {
            totp.secondsRemaining.toFloat() / 30f
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            progress = { targetProgress },
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            strokeWidth = 4.dp,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = otp.accountIssuer,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Text(
                text = otp.accountName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Text(
                text = totp.code,
                style = MaterialTheme.typography.numeralSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun PageOtpScreen(
    httpClient: HttpClient = koinInject(),
) {
    var allOneTimePasswords by remember { mutableStateOf(emptyList<SimpleOneTimePassword>()) }

    var isLoading by remember { mutableStateOf(true) }
    var loadingFailed by remember { mutableStateOf(false) }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val otps = getOneTimePasswords(httpClient, context)
            val allOtps =
                otps.myOneTimePasswords.map { SimpleOneTimePassword.fromOneTimePassword(it) }
                    .toMutableList()
            for (otp in otps.sharedOneTimePasswords) {
                allOtps += SimpleOneTimePassword.fromSharedOneTimePassword(otp)
            }

            allOneTimePasswords = allOtps

            loadingFailed = false
        } catch (ex: Exception) {
            loadingFailed = true
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val pagerState =
            rememberPagerState(pageCount = { allOneTimePasswords.size })

        VerticalPagerScaffold(pagerState = pagerState) {
            VerticalPager(
                state = pagerState,
                flingBehavior =
                    PagerScaffoldDefaults.snapWithSpringFlingBehavior(
                        state = pagerState
                    ),
            ) { page ->
                AnimatedPage(
                    pageIndex = page,
                    pagerState = pagerState
                ) {
                    ScreenScaffold {
                        OtpItem(allOneTimePasswords[page])
                    }
                }
            }
        }
    }
}