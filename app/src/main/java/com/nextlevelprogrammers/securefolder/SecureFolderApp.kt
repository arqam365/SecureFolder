package com.nextlevelprogrammers.securefolder

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AppScreen(context: ComponentActivity) {
    var currentScreen by remember { mutableStateOf(Screen.Setup) }

    when (currentScreen) {
        else -> SetupScreen(context) { currentScreen = Screen.Setup }
    }
}

@Composable
fun SetupScreen(context: ComponentActivity, onCodeSet: () -> Unit) {
    var code by remember { mutableStateOf("") }
    var isCodeSet by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (!isCodeSet) {
            TextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Enter Unique Code") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (code.isNotEmpty()) {
                        storeHashedCode(context, code)
                        isCodeSet = true
                        Toast.makeText(context, "Code set successfully!", Toast.LENGTH_SHORT).show()
                        onCodeSet()
                    } else {
                        Toast.makeText(context, "Please enter a code", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Code")
            }
        } else {
            Text("Code has been set.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showPasswordDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Encrypt File")
        }

        if (showPasswordDialog) {
            PasswordDialog(onPasswordEntered = { password ->
                // Handle encryption with the entered password
                showPasswordDialog = false
            })
        }
    }
}

@Composable
fun AuthenticationScreen(context: ComponentActivity, onAuthentication: (Boolean) -> Unit) {
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Enter Unique Code") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (verifyCode(context, code)) {
                    onAuthentication(true)
                } else {
                    Toast.makeText(context, "Invalid Code", Toast.LENGTH_SHORT).show()
                    onAuthentication(false)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Authenticate")
        }
    }
}

@Composable
fun MainAppScreen(context: ComponentActivity) {
    var hiddenData by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                hiddenData = readEncryptedData(context, "hidden_file.txt") ?: "No data found"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Load Hidden Data")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Hidden Data:")
        Spacer(modifier = Modifier.height(8.dp))
        Text(hiddenData)
    }
}

@Composable
fun PasswordDialog(onPasswordEntered: (String) -> Unit) {
    var password by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Enter Password") },
        text = {
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
        },
        confirmButton = {
            Button(onClick = { onPasswordEntered(password) }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = { /* Dismiss */ }) {
                Text("Cancel")
            }
        }
    )
}

sealed class Screen {
    object Setup : Screen()
    object Authentication : Screen()
    object Main : Screen()
}
