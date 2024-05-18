package dev.imanuel.jewels.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Type::class)
object TypeSerializer : KSerializer<Type> {
    override fun deserialize(decoder: Decoder): Type {
        return Type.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Type) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
data class Drive(val name: String, val driver: String, val manufacturer: String, val model: String, val size: Float)

@Serializable
data class Cpu(val manufacturer: String, val model: String, val speed: Float?, val cores: Int, val threads: Int)

@Serializable
data class Mainboard(val manufacturer: String, val version: String, val model: String, val serial: String)

@Serializable
data class Kernel(val release: String, val version: String, val architecture: String)

@Serializable
data class OperatingSystem(val version: String?, val name: String)

@Serializable(with = TypeSerializer::class)
enum class Type {
    PhoneOrTablet, Computer, Smartwatch, Other;

    override fun toString(): String {
        return when (this) {
            Type.Computer -> {
                "computer"
            }

            Type.Smartwatch -> {
                "watch"
            }

            Type.Other -> {
                "other"
            }

            else -> {
                "phone"
            }
        }
    }

    companion object {
        fun fromString(value: String): Type {
            return when (value) {
                "computer" -> {
                    Type.Computer
                }

                "watch" -> {
                    Type.Smartwatch
                }

                "other" -> {
                    Type.Other
                }

                else -> {
                    Type.PhoneOrTablet
                }
            }
        }
    }
}

@Serializable
data class Device(
    val id: String,
    val type: Type,
    val hostname: String?,
    val model: String,
    val manufacturer: String,
    val os: OperatingSystem?,
    val storage: Float?,
    val ram: Float?,
    val cpu: Cpu?,
    val mainboard: Mainboard?,
    val kernel: Kernel?,
    val drives: List<Drive>?
) {
}
