package dev.imanuel.jewels.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.isSuccess

suspend fun getOneTimePasswords(httpClient: HttpClient): OneTimePasswords {
    val res = httpClient.get {
        url("one-time-password")
    }

    if (res.status.isSuccess())
        return res.body()
    else
        throw Exception("Failed to fetch one-time passwords")
}

suspend fun deleteOneTimePassword(id: Long, httpClient: HttpClient): Boolean {
    val res = httpClient.delete {
        url("one-time-password/$id")
    }

    return res.status.isSuccess()
}

suspend fun updateOneTimePassword(id: Long, name: String, httpClient: HttpClient): Boolean {
    val res = httpClient.put {
        url("one-time-password/${id}")
        setBody(mapOf("accountName" to name))
    }

    return res.status.isSuccess()
}

suspend fun createOneTimePassword(
    accountName: String,
    accountIssuer: String,
    secretKey: String,
    httpClient: HttpClient
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

    if (res.status.isSuccess())
        return res.body()
    else
        throw Exception("Failed to create one-time password")
}

suspend fun shareOneTimePassword(
    id: Long,
    sharedWith: List<Long>,
    httpClient: HttpClient
): Boolean {
    val res = httpClient.post {
        url("one-time-password/${id}/share")
        setBody(mapOf("sharedWith" to sharedWith))
    }

    return res.status.isSuccess()
}

suspend fun getUsers(httpClient: HttpClient): List<User> {
    val res = httpClient.get {
        url("owner/other")
    }

    return res.body()
}
