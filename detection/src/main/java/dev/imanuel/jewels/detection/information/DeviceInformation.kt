package dev.imanuel.jewels.detection.information

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
enum class DeviceType {
    Watch,
    Handheld
}

@Serializable
data class Drive(val name: String, val driver: String, val manufacturer: String, val model: String, val size: Float)

@Serializable
data class Cpu(val manufacturer: String, val model: String, val speed: Float?, val cores: Int, val threads: Int)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Mainboard(val manufacturer: String, val version: String, val model: String)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Kernel(val version: String, val architecture: String)

@Serializable
data class OperatingSystem(val version: String?, val name: String)

@Serializable
data class Device(
    val id: String,
    val hostname: String?,
    val model: String,
    val manufacturer: String,
    val os: OperatingSystem?,
    val storage: Float?,
    val ram: Float?,
    val cpu: Cpu?,
    val mainboard: Mainboard?,
    val kernel: Kernel?,
    val drives: List<Drive>?,
    val type: DeviceType,
)
