package dev.imanuel.jewels.api

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.ConfigurationV0
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.io.File
import java.security.MessageDigest

class EncryptedJsonDiskCache(
    context: Context,
    private val cacheDir: File = File(context.cacheDir, "offline_json_cache"),
) {
    private val aead: Aead

    init {
        AeadConfig.register()

        cacheDir.mkdirs()

        val masterKeyUri = "android-keystore://offline-cache-master-key"

        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, "offline_cache_keyset", "offline_cache_keyset_handle")
            .withKeyTemplate(AeadKeyTemplates.CHACHA20_POLY1305)
            .withMasterKeyUri(masterKeyUri)
            .build()
            .keysetHandle

        aead = keysetHandle.getPrimitive(ConfigurationV0.get(), Aead::class.java)
    }

    fun put(cacheKey: String, json: String) {
        val file = fileForKey(cacheKey)
        val plaintext = json.toByteArray(Charsets.UTF_8)

        val aad = cacheKey.toByteArray(Charsets.UTF_8)
        val ciphertext = aead.encrypt(plaintext, aad)

        file.writeBytes(ciphertext)
    }

    fun get(cacheKey: String): String? {
        val file = fileForKey(cacheKey)
        if (!file.exists()) return null

        val aad = cacheKey.toByteArray(Charsets.UTF_8)
        val plaintext = aead.decrypt(file.readBytes(), aad)
        return plaintext.toString(Charsets.UTF_8)
    }

    private fun fileForKey(cacheKey: String): File {
        val safe = sha256Hex(cacheKey)
        return File(cacheDir, safe)
    }

    private fun sha256Hex(s: String): String {
        val d = MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
        return d.joinToString("") { "%02x".format(it) }
    }
}
