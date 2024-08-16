package com.nextlevelprogrammers.securefolder

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.nextlevelprogrammers.securefolder.ui.theme.SecureFolderTheme
import java.io.File

fun storeHashedCode(context: Context, code: String) {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    val hashedCode = code.hashCode().toString()
    sharedPreferences.edit().putString("unique_code", hashedCode).apply()
}

fun verifyCode(context: Context, enteredCode: String): Boolean {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    val storedHashedCode = sharedPreferences.getString("unique_code", null)
    return storedHashedCode == enteredCode.hashCode().toString()
}

fun storeEncryptedData(context: Context, fileName: String, data: String) {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val file = File(context.filesDir, fileName)
    val encryptedFile = EncryptedFile.Builder(
        file,
        context,
        masterKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()

    encryptedFile.openFileOutput().use { outputStream ->
        outputStream.write(data.toByteArray())
    }
}

fun readEncryptedData(context: Context, fileName: String): String? {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val file = File(context.filesDir, fileName)
    val encryptedFile = EncryptedFile.Builder(
        file,
        context,
        masterKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()

    return encryptedFile.openFileInput().use { inputStream ->
        inputStream.readBytes().toString(Charsets.UTF_8)
    }
}

fun isCodeSetUp(context: Context): Boolean {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    return sharedPreferences.contains("unique_code")
}

fun navigateToMainAppScreen(context: ComponentActivity) {
    context.setContent {
        SecureFolderTheme {
            MainAppScreen(context)
        }
    }
}

fun hideApp(context: ComponentActivity) {
    // This is a placeholder. Actual implementation will depend on specific needs.
    // Example: Move to background
    context.moveTaskToBack(true)
}
