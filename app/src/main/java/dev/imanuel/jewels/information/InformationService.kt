package dev.imanuel.jewels.information

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.imanuel.jewels.utils.ServerSettings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

@Serializable
data class PushData(val message: String?)

class PushException(override val message: String?) : Exception(message)

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: HttpException) {
            if (e.code() == 404 || e.code() == 401) {
                val message = (e.response()?.body() as PushData?)?.message

                throw PushException(message)
            }

            throw e
        }
    }
}

fun getRetrofit(serverSettings: ServerSettings): Retrofit {
    val client = OkHttpClient.Builder()
        .addInterceptor(ErrorInterceptor())
        .build()

    return Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(client)
        .baseUrl(serverSettings.host)
        .build()
}

interface ApiService {
    @POST("api/device/phone")
    suspend fun sendPhoneData(@Header("Authorization") token: String, @Body device: Device): Unit

    @POST("api/device/watch")
    suspend fun sendWatchData(@Header("Authorization") token: String, @Body device: Device): Unit
}
