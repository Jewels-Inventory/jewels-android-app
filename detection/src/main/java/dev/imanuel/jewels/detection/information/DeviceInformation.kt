@file:OptIn(ExperimentalSerializationApi::class)
package dev.imanuel.jewels.detection.information

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonNames

@Serializable
enum class DeviceType {
    @JsonNames("watch")
    Watch,
    @JsonNames("phone")
    Handheld,
    @JsonNames("computer")
    Computer,
    @JsonNames("other")
    Other
}

@Serializable
data class Drive(
    val name: String,
    val manufacturer: String,
    val model: String,
    val size: Float
)

@Serializable
data class Cpu(
    val manufacturer: String,
    val model: String,
    val speed: Float?,
    val cores: Int,
    val threads: Int
)

@Serializable
data class Mainboard(val manufacturer: String, val version: String, val model: String)

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
