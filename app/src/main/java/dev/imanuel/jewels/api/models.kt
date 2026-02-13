@file:OptIn(ExperimentalSerializationApi::class)

package dev.imanuel.jewels.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class OneTimePasswords(
    @SerialName("myOneTimePasswords")
    val myOneTimePasswords: List<OneTimePassword> = emptyList(),

    @SerialName("sharedOneTimePasswords")
    val sharedOneTimePasswords: List<SharedOneTimePassword> = emptyList(),
)

@Serializable
@JsonIgnoreUnknownKeys
data class OneTimePassword(
    @SerialName("id")
    val id: Long,

    @SerialName("accountName")
    var accountName: String,

    @SerialName("accountIssuer")
    val accountIssuer: String,

    @SerialName("secretKey")
    val secretKey: String,

    @SerialName("canEdit")
    val canEdit: Boolean,

    @SerialName("brandIcon")
    val brandIcon: String,

    @SerialName("simpleIcon")
    val simpleIcon: String,

    @SerialName("brandIconSimilarity")
    val brandIconSimilarity: Double,

    @SerialName("simpleIconSimilarity")
    val simpleIconSimilarity: Double,

    @SerialName("sharedWith")
    val sharedWith: List<User> = emptyList(),
)

@Serializable
@JsonIgnoreUnknownKeys
data class SharedOneTimePassword(
    @SerialName("id")
    val id: Long,

    @SerialName("accountName")
    val accountName: String,

    @SerialName("accountIssuer")
    val accountIssuer: String,

    @SerialName("secretKey")
    val secretKey: String,

    @SerialName("canEdit")
    val canEdit: Boolean,

    @SerialName("brandIcon")
    val brandIcon: String,

    @SerialName("simpleIcon")
    val simpleIcon: String,

    @SerialName("brandIconSimilarity")
    val brandIconSimilarity: Double,

    @SerialName("simpleIconSimilarity")
    val simpleIconSimilarity: Double,

    @SerialName("sharedBy")
    val sharedBy: User,
)

@Serializable
@JsonIgnoreUnknownKeys
data class User(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("isAdmin")
    val isAdmin: Boolean,

    @SerialName("profilePicture")
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