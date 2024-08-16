package com.nextlevelprogrammers.securefolder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.UUID

class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: EncryptedSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize EncryptedSharedPreferences
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            this,
            "secure_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        setContent {
            AppScreen(this)
        }
    }

    private fun encryptFile(fileUri: Uri, password: String) {
        val inputStream = contentResolver.openInputStream(fileUri)
        val data = inputStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() } ?: return
        inputStream?.close()

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val file = File(filesDir, "encrypted_${UUID.randomUUID()}.txt")

        val encryptedFile = EncryptedFile.Builder(
            file,
            this,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        try {
            encryptedFile.openFileOutput().use { outputStream ->
                outputStream.write(data.toByteArray())
            }
            Toast.makeText(this, "File encrypted successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Encryption failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readEncryptedData(fileName: String): String? {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val file = File(filesDir, fileName)

        val encryptedFile = EncryptedFile.Builder(
            file,
            this,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return try {
            encryptedFile.openFileInput().bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun storeHashedCode(context: ComponentActivity, code: String) {
        val hashedCode = code.hashCode().toString()  // Simple hash, replace with a better hash function if needed
        sharedPreferences.edit().putString("user_code", hashedCode).apply()
    }

    private fun verifyCode(context: ComponentActivity, code: String): Boolean {
        val hashedCode = code.hashCode().toString()  // Hash the input code
        return hashedCode == sharedPreferences.getString("user_code", null)
    }

    private fun navigateToMainAppScreen(context: ComponentActivity) {
        val intent = Intent(context, MainActivity::class.java) // Changed to MainScreenActivity
        context.startActivity(intent)
    }
}
