package dev.imanuel.jewels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AnimatedPage
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.PagerScaffoldDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.VerticalPagerScaffold
import dev.imanuel.jewels.shared.api.ApiClient
import dev.imanuel.jewels.shared.api.SimpleOneTimePassword
import dev.imanuel.jewels.shared.otp.rememberTotpState
import org.koin.compose.koinInject

@Composable
fun OtpItem(otp: SimpleOneTimePassword) {
    val totp by rememberTotpState(otp.secretKey)

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
    apiClient: ApiClient = koinInject()
) {
    var allOneTimePasswords by remember { mutableStateOf(emptyList<SimpleOneTimePassword>()) }

    var isLoading by remember { mutableStateOf(true) }
    var loadingFailed by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { allOneTimePasswords.size })

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val otps = apiClient.getOneTimePasswords()
            val allOtps = otps
                .myOneTimePasswords
                .map { SimpleOneTimePassword.fromOneTimePassword(it) }
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
        VerticalPagerScaffold(pagerState = pagerState) {
            VerticalPager(
                state = pagerState,
                flingBehavior = PagerScaffoldDefaults.snapWithSpringFlingBehavior(
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