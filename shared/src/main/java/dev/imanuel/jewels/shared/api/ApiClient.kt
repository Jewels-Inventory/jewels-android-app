package dev.imanuel.jewels.shared.api

import android.content.Context
import dev.imanuel.jewels.shared.information.Device
import dev.imanuel.jewels.shared.information.DeviceType
import dev.imanuel.jewels.shared.loadSettings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.concurrent.thread

class ApiClient(
    private val context: Context,
    private val httpClient: HttpClient,
    private val encryptedJsonDiskCache: EncryptedJsonDiskCache,
    private val json: Json
) {
    suspend fun sendData(device: Device) {
        val deviceType = if (device.type == DeviceType.Handheld) "phone" else "watch"
        val settings = context.loadSettings();
        if (settings != null) {
            try {
                httpClient.post("device/${deviceType}") {
                    setBody(device)
                }
            } catch (ex: Exception) {
                throw PushException(ex.message)
            }
        }
    }

    suspend fun checkServer(): Boolean {
        val res = httpClient.get {
            url("healthz")
        }

        return res.status.isSuccess() && res.bodyAsText() == "OK"
    }

    fun clearCache() {
        encryptedJsonDiskCache.clear()
    }

    suspend fun getCached(url: String): String {
        return try {
            val res = httpClient.get {
                url(url)
            }
            val json = res.bodyAsText()

            if (res.status.isSuccess()) {
                encryptedJsonDiskCache.put(url, json)
                json
            } else {
                ""
            }
        } catch (e: Exception) {
            encryptedJsonDiskCache.get(url) ?: throw e
        }
    }

    suspend fun getOneTimePasswords(): OneTimePasswords {
        return json.decodeFromString(getCached("one-time-password"))
    }

    suspend fun deleteOneTimePassword(id: Long): Boolean {
        val res = httpClient.delete {
            url("one-time-password/$id")
        }

        val success = res.status.isSuccess()
        if (success) {
            thread {
                runBlocking {
                    getCached("one-time-password")
                }
            }
        }

        return success
    }

    suspend fun updateOneTimePassword(
        id: Long,
        name: String,
    ): Boolean {
        val res = httpClient.put {
            url("one-time-password/${id}")
            setBody(mapOf("accountName" to name))
        }

        val success = res.status.isSuccess()
        if (success) {
            thread {
                runBlocking {
                    getCached("one-time-password")
                }
            }
        }

        return success
    }

    suspend fun createOneTimePassword(
        accountName: String,
        accountIssuer: String,
        secretKey: String,
    ): OneTimePassword {
        val res = httpClient.post {
            url("one-time-password")
            setBody(
                mapOf(
                    "accountName" to accountName,
                    "accountIssuer" to accountIssuer,
                    "secretKey" to secretKey
                )
            )
        }

        val success = res.status.isSuccess()
        if (success) {
            thread {
                runBlocking {
                    getCached("one-time-password")
                }
            }

            return res.body()
        } else
            throw Exception("Failed to create one-time password")
    }

    suspend fun shareOneTimePassword(
        id: Long,
        sharedWith: List<Long>,
    ): Boolean {
        val res = httpClient.post {
            url("one-time-password/${id}/share")
            setBody(mapOf("sharedWith" to sharedWith))
        }

        val success = res.status.isSuccess()
        if (success) {
            thread {
                runBlocking {
                    getCached("one-time-password")
                }
            }
        }

        return success

    }

    suspend fun getUsers(): List<User> {
        val res = httpClient.get {
            url("owner/other")
        }

        return res.body()
    }

    suspend fun getEolDevices(): Map<String, List<DeviceInformation>> {
        val res = httpClient.get {
            url("eol")
        }

        return res.body()
    }

    suspend fun getMyJewels(): List<Device> {
        return json.decodeFromString(getCached("my-jewels"))
    }
}
