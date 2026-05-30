package com.groceryoverview.update

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.groceryoverview.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val releaseNotes: String? = null
)

sealed interface UpdateCheckResult {
    data object UpToDate : UpdateCheckResult
    data class UpdateAvailable(val info: UpdateInfo) : UpdateCheckResult
    data class Unavailable(val message: String) : UpdateCheckResult
}

class AppUpdateManager(
    private val context: Context
) {
    private val appContext = context.applicationContext

    suspend fun checkForUpdate(): UpdateCheckResult = withContext(Dispatchers.IO) {
        runCatching {
            val updateInfo = fetchUpdateInfo()
            if (updateInfo.versionCode > BuildConfig.VERSION_CODE) {
                UpdateCheckResult.UpdateAvailable(updateInfo)
            } else {
                UpdateCheckResult.UpToDate
            }
        }.getOrElse { throwable ->
            UpdateCheckResult.Unavailable(throwable.message ?: "Unable to check for updates.")
        }
    }

    suspend fun downloadAndInstall(updateInfo: UpdateInfo): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val apkFile = downloadApk(updateInfo.apkUrl)
            val contentUri = FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.fileprovider",
                apkFile
            )

            withContext(Dispatchers.Main) {
                val installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                    setDataAndType(contentUri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                appContext.startActivity(installIntent)
            }
            Result.success(Unit)
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }

    private fun fetchUpdateInfo(): UpdateInfo {
        val manifestUrl = BuildConfig.UPDATE_MANIFEST_URL
        require(manifestUrl.isNotBlank()) {
            "Set UPDATE_MANIFEST_URL in app/build.gradle.kts before using the update button."
        }

        val urlConnection = URL(manifestUrl).openConnection() as HttpURLConnection
        return try {
            urlConnection.connectTimeout = 10_000
            urlConnection.readTimeout = 10_000
            urlConnection.requestMethod = "GET"
            urlConnection.inputStream.bufferedReader().use { reader ->
                val json = JSONObject(reader.readText())
                UpdateInfo(
                    versionCode = json.getInt("versionCode"),
                    versionName = json.getString("versionName"),
                    apkUrl = json.getString("apkUrl"),
                    releaseNotes = json.optString("releaseNotes").takeIf { it.isNotBlank() }
                )
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun downloadApk(apkUrl: String): File {
        val updatesDir = File(appContext.cacheDir, "updates").apply {
            mkdirs()
        }
        val targetFile = File(updatesDir, "grocery-overview-update.apk")
        val urlConnection = URL(apkUrl).openConnection() as HttpURLConnection

        return try {
            urlConnection.connectTimeout = 15_000
            urlConnection.readTimeout = 60_000
            urlConnection.requestMethod = "GET"

            urlConnection.inputStream.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            targetFile
        } finally {
            urlConnection.disconnect()
        }
    }
}
