@file:OptIn(ExperimentalSerializationApi::class)

package dev.imanuel.jewels.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlin.time.Instant

@Serializable
@JsonIgnoreUnknownKeys
data class OneTimePasswords(
    val myOneTimePasswords: List<OneTimePassword> = emptyList(),
    val sharedOneTimePasswords: List<SharedOneTimePassword> = emptyList(),
)

@Serializable
@JsonIgnoreUnknownKeys
data class OneTimePassword(
    val id: Long,
    var accountName: String,
    val accountIssuer: String,
    val secretKey: String,
    val canEdit: Boolean,
    val brandIcon: String,
    val simpleIcon: String,
    val brandIconSimilarity: Double,
    val simpleIconSimilarity: Double,
    val sharedWith: List<User> = emptyList(),
)

@Serializable
@JsonIgnoreUnknownKeys
data class SharedOneTimePassword(
    val id: Long,
    val accountName: String,
    val accountIssuer: String,
    val secretKey: String,
    val canEdit: Boolean,
    val brandIcon: String,
    val simpleIcon: String,
    val brandIconSimilarity: Double,
    val simpleIconSimilarity: Double,
    val sharedBy: User,
)

@Serializable
@JsonIgnoreUnknownKeys
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val isAdmin: Boolean,
    val profilePicture: String,
)

data class SimpleOneTimePassword(
    val id: Long,
    val accountName: String,
    val accountIssuer: String,
    val secretKey: String,
    val canEdit: Boolean,
    val iconSource: String,
    val sharedWith: List<User> = emptyList(),
) {
    companion object {
        fun fromOneTimePassword(otp: OneTimePassword): SimpleOneTimePassword {
            val iconSource =
                if (otp.brandIconSimilarity == 0.0 && otp.simpleIconSimilarity == 0.0) {
                    "/static/img/default.svg"
                } else if (otp.brandIconSimilarity > otp.simpleIconSimilarity) {
                    "/api/icons/${otp.brandIcon}"
                } else {
                    "/api/icons/${otp.simpleIcon}"
                }

            return SimpleOneTimePassword(
                otp.id,
                otp.accountName,
                otp.accountIssuer,
                otp.secretKey,
                otp.canEdit,
                iconSource,
                otp.sharedWith,
            )
        }

        fun fromSharedOneTimePassword(otp: SharedOneTimePassword): SimpleOneTimePassword {
            val iconSource =
                if (otp.brandIconSimilarity == 0.0 && otp.simpleIconSimilarity == 0.0) {
                    "/static/img/default.svg"
                } else if (otp.brandIconSimilarity > otp.simpleIconSimilarity) {
                    "/api/icons/${otp.brandIcon}"
                } else {
                    "/api/icons/${otp.simpleIcon}"
                }

            return SimpleOneTimePassword(
                otp.id,
                otp.accountName,
                otp.accountIssuer,
                otp.secretKey,
                otp.canEdit,
                iconSource,
                emptyList(),
            )
        }
    }
}

@Serializable
@JsonIgnoreUnknownKeys
data class Device(
    val id: String,
    val type: String,
    val hostname: String,
    val model: String,
    val manufacturer: String,
    val storage: Double,
    val ram: Double,
    val eol: Instant
)
