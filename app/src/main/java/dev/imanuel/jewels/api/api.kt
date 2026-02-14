package dev.imanuel.jewels.api

import android.content.Context
import android.util.Log
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

suspend fun cacheOneTimePasswords(httpClient: HttpClient, context: Context): Pair<Boolean, String> {
    val cache = EncryptedJsonDiskCache(context)
    val cacheKey = "one-time-passwords"

    return try {
        val res = httpClient.get {
            url("one-time-password")
        }
        val json = res.bodyAsText()

        if (res.status.isSuccess()) {
            cache.put(cacheKey, json)
            true to json
        } else {
            false to ""
        }
    } catch (e: Exception) {
        false to (cache.get(cacheKey) ?: throw e)
    }
}

suspend fun getOneTimePasswords(httpClient: HttpClient, context: Context): OneTimePasswords {
    return Json.decodeFromString(cacheOneTimePasswords(httpClient, context).second)
}

suspend fun deleteOneTimePassword(id: Long, httpClient: HttpClient, context: Context): Boolean {
    val res = httpClient.delete {
        url("one-time-password/$id")
    }

    val success = res.status.isSuccess()
    if (success) {
        thread {
            runBlocking {
                cacheOneTimePasswords(httpClient, context)
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
                cacheOneTimePasswords(httpClient, context)
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
                cacheOneTimePasswords(httpClient, context)
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
                cacheOneTimePasswords(httpClient, context)
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

    try {
        return res.body()
    } catch (ex: Exception) {
        Log.e("getEolDevices", ex.message!!)
        Log.e("getEolDevices", ex.stackTraceToString())
        throw ex
    }
}
