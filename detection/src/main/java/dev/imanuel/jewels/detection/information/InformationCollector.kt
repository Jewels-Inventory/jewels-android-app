package dev.imanuel.jewels.detection.information

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import android.provider.Settings
import dev.imanuel.jewels.detection.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile
import java.util.UUID
import androidx.core.content.edit

private data class RetailDeviceDetails(
    val manufacturer: String,
    val retailName: String,
)

interface InformationCollector {
    suspend fun collect(type: DeviceType): Device
}

private const val CPU_INFO_DIR = "/sys/devices/system/cpu/"
private const val ONE_GIB = 1024f * 1024f * 1024f
private const val ONE_GB = 1000f * 1000f * 1000f
private const val UNKNOWN = "Unbekannt"

private fun String.upperFirst(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

class InformationCollectorImpl(private val context: Context) : InformationCollector {
    private fun getId(): String {
        val pref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val id =
            pref.getString("app_id", UUID.randomUUID().toString()) ?: UUID.randomUUID().toString()
        pref.edit { putString("app_id", id) }

        return id
    }

    override suspend fun collect(type: DeviceType): Device {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val storageStatsManager =
            context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager

        val maxPath = "${CPU_INFO_DIR}cpu0/cpufreq/cpuinfo_max_freq"
        val cpuSpeed = try {
            withContext(Dispatchers.IO) {
                RandomAccessFile(maxPath, "r").use { it.readLine().toLong() / 1000f / 1000f }
            }
        } catch (e: Exception) {
            null
        }
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val drives = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            storageManager.storageVolumes.map {
                val storageUuid = it.uuid
                if (it.isPrimary) {
                    Drive(
                        name = "Interner Speicher",
                        manufacturer = UNKNOWN,
                        size = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT) / ONE_GIB,
                        model = UNKNOWN
                    )
                } else if (storageUuid != null) {
                    Drive(
                        name = it.getDescription(context),
                        manufacturer = UNKNOWN,
                        size = (it.directory?.totalSpace ?: 0) / ONE_GB,
                        model = UNKNOWN
                    )
                } else {
                    null
                }
            }
        } else {
            context.getExternalFilesDirs(null).map {
                val storageVolume = storageManager.getStorageVolume(it)
                if (storageVolume == null) {
                    null
                } else {
                    if (storageVolume.isPrimary) {
                        Drive(
                            name = "Interner Speicher",
                            manufacturer = UNKNOWN,
                            size = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT) / ONE_GIB,
                            model = UNKNOWN
                        )
                    } else {
                        Drive(
                            name = storageVolume.getDescription(context),
                            manufacturer = UNKNOWN,
                            size = it.totalSpace / ONE_GB,
                            model = UNKNOWN
                        )
                    }
                }
            }
        }.filterNotNull()
        val storage = drives.sumOf { it.size.toDouble() }.toFloat()

        val mainboard = Mainboard(
            manufacturer = Build.MANUFACTURER.upperFirst(),
            model = Build.BOARD,
            version = UNKNOWN,
        )
        val cpu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Cpu(
                manufacturer = Build.SOC_MANUFACTURER.upperFirst(),
                model = Build.SOC_MODEL,
                cores = Runtime.getRuntime().availableProcessors(),
                speed = cpuSpeed,
                threads = Runtime.getRuntime().availableProcessors()
            )
        } else {
            null
        }
        val ram = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            memoryInfo.advertisedMem / ONE_GIB
        } else {
            memoryInfo.totalMem / ONE_GIB
        }
        val os = OperatingSystem(
            name = "Android",
            version = Build.VERSION.RELEASE,
        )
        val kernel = Kernel(
            version = System.getProperty("os.version") ?: "Unbekannt",
            architecture = Build.SUPPORTED_ABIS[0]
        )

        return Device(
            getId(),
            Settings.Global.getString(context.contentResolver, "device_name"),
            Build.MODEL,
            "",
            os,
            storage,
            ram,
            cpu,
            mainboard,
            kernel,
            drives,
            type
        )
    }
}
