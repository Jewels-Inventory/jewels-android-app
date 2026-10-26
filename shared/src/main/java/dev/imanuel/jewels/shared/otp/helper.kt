package dev.imanuel.jewels.shared.otp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds


fun generateOtp(secretKey: String): String {
    return GoogleAuthenticator(secretKey.encodeToByteArray()).generate()
}

data class CurrentState(val code: String, val secondsRemaining: Int)

@Composable
fun rememberTotpState(
    secretKey: String,
) = produceState(CurrentState("", 0), secretKey) {
    var lastStep: Long = -1L
    val periodSeconds = 30

    while (isActive) {
        val nowSec = System.currentTimeMillis() / 1000L
        val step = nowSec / periodSeconds
        val secondsRemaining = (periodSeconds - (nowSec % periodSeconds)).toInt()

        if (step != lastStep) {
            lastStep = step
            val newCode = generateOtp(secretKey)
            value = CurrentState(newCode, secondsRemaining)
        } else {
            value = CurrentState(value.code, secondsRemaining)
        }

        delay(1_000.milliseconds)
    }
}
