package dev.imanuel.jewels.utils

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import android.provider.Settings
import android.provider.Settings.Secure
import dev.imanuel.jewels.R
import java.io.RandomAccessFile

data class RetailDeviceDetails(
    val manufacturer: String,
    val retailName: String,
)

private const val CPU_INFO_DIR = "/sys/devices/system/cpu/"
private const val ONE_GB = 1024f * 1024f * 1024f
private const val UNKNOWN = "Unbekannt"

fun getRetailDeviceDetails(context: Context): RetailDeviceDetails {
    val devices = context.resources.openRawResource(R.raw.supported_devices).bufferedReader(Charsets.UTF_16)
    val matchingDevice = devices.lines().filter {
        it.isNotBlank() && it.matches(Regex("^.*,.*,${Build.DEVICE},${Build.MODEL}\$"))
    }.findFirst()

    return if (matchingDevice.isPresent) {
        val splitDeviceString = matchingDevice.get().split(',')
        RetailDeviceDetails(
            manufacturer = splitDeviceString.getOrElse(0) { Build.MANUFACTURER },
            retailName = splitDeviceString.getOrElse(1) { Build.MODEL },
        )
    } else {
        RetailDeviceDetails(
            manufacturer = Build.MANUFACTURER,
            retailName = Build.MODEL,
        )
    }
}

fun String.upperFirst(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

fun collectPhoneOrTabletDeviceInformation(context: Context): Device {
    val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    val maxPath = "${CPU_INFO_DIR}cpu0/cpufreq/cpuinfo_max_freq"
    val cpuSpeed = try {
        RandomAccessFile(maxPath, "r").use { it.readLine().toLong() / 1000f / 1000f }
    } catch (e: Exception) {
        null
    }
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val drives = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        storageManager.storageVolumes.map {
            val storageUuid = it.storageUuid
            if (it.isPrimary) {
                Drive(
                    name = "Interner Speicher",
                    driver = UNKNOWN,
                    manufacturer = UNKNOWN,
                    size = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT) / ONE_GB,
                    model = UNKNOWN
                )
            } else if (storageUuid != null) {
                Drive(
                    name = it.getDescription(context),
                    driver = UNKNOWN,
                    manufacturer = UNKNOWN,
                    size = storageStatsManager.getTotalBytes(storageUuid) / ONE_GB,
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
                        driver = UNKNOWN,
                        manufacturer = UNKNOWN,
                        size = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT) / ONE_GB,
                        model = UNKNOWN
                    )
                } else {
                    Drive(
                        name = storageVolume.getDescription(context),
                        driver = UNKNOWN,
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
        serial = UNKNOWN
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
        memoryInfo.advertisedMem / ONE_GB
    } else {
        memoryInfo.totalMem / ONE_GB
    }
    val os = OperatingSystem(
        name = "Android",
        version = Build.VERSION.RELEASE,
    )
    val kernel = Kernel(
        release = System.getProperty("os.version") ?: "Unbekannt",
        version = System.getProperty("os.version") ?: "Unbekannt",
        architecture = Build.SUPPORTED_ABIS[0]
    )
    val retailDevice = getRetailDeviceDetails(context)

    return Device(
        Secure.ANDROID_ID,
        Type.PhoneOrTablet,
        Settings.Global.getString(context.contentResolver, "device_name"),
        retailDevice.retailName,
        retailDevice.manufacturer,
        os,
        storage,
        ram,
        cpu,
        mainboard,
        kernel,
        drives,
    )
}