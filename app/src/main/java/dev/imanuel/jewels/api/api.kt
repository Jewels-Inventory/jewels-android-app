package dev.imanuel.jewels.api

import android.content.Context
import dev.imanuel.jewels.pages.json
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
import kotlin.concurrent.thread
import dev.imanuel.jewels.detection.information.Device as DeviceInformation

suspend fun checkServer(httpClient: HttpClient): Boolean {
    val res = httpClient.get {
        url("healthz")
    }

    return res.status.isSuccess() && res.bodyAsText() == "OK"
}

suspend fun getCached(
    httpClient: HttpClient,
    context: Context,
    url: String
): String {
    val cache = EncryptedJsonDiskCache(context)

    return try {
        val res = httpClient.get {
            url(url)
        }
        val json = res.bodyAsText()

        if (res.status.isSuccess()) {
            cache.put(url, json)
            json
        } else {
            ""
        }
    } catch (e: Exception) {
        cache.get(url) ?: throw e
    }
}

suspend fun getOneTimePasswords(httpClient: HttpClient, context: Context): OneTimePasswords {
    return json.decodeFromString(getCached(httpClient, context, "one-time-password"))
}

suspend fun deleteOneTimePassword(id: Long, httpClient: HttpClient, context: Context): Boolean {
    val res = httpClient.delete {
        url("one-time-password/$id")
    }

    val success = res.status.isSuccess()
    if (success) {
        thread {
            runBlocking {
                getCached(httpClient, context, "one-time-password")
            }
        }
    }

    return success
}

suspend fun updateOneTimePassword(
    id: Long,
    name: String,
    httpClient: HttpClient,
    context: Context
): Boolean {
    val res = httpClient.put {
        url("one-time-password/${id}")
        setBody(mapOf("accountName" to name))
    }

    val success = res.status.isSuccess()
    if (success) {
        thread {
            runBlocking {
                getCached(httpClient, context, "one-time-password")
            }
        }
    }

    return success
}

suspend fun createOneTimePassword(
    accountName: String,
    accountIssuer: String,
    secretKey: String,
    httpClient: HttpClient,
    context: Context
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
                getCached(httpClient, context, "one-time-password")
            }
        }

        return res.body()
    } else
        throw Exception("Failed to create one-time password")
}

suspend fun shareOneTimePassword(
    id: Long,
    sharedWith: List<Long>,
    httpClient: HttpClient,
    context: Context
): Boolean {
    val res = httpClient.post {
        url("one-time-password/${id}/share")
        setBody(mapOf("sharedWith" to sharedWith))
    }

    val success = res.status.isSuccess()
    if (success) {
        thread {
            runBlocking {
                getCached(httpClient, context, "one-time-password")
            }
        }
    }

    return success

}

suspend fun getUsers(httpClient: HttpClient): List<User> {
    val res = httpClient.get {
        url("owner/other")
    }

    return res.body()
}

suspend fun getEolDevices(httpClient: HttpClient): Map<String, List<Device>> {
    val res = httpClient.get {
        url("eol")
    }

    return res.body()
}

suspend fun getMyJewels(httpClient: HttpClient, context: Context): List<DeviceInformation> {
    return json.decodeFromString(getCached(httpClient, context, "my-jewels"))
}
