package com.nextlevelprogrammers.securefolder.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SecureFolderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecureFolderTheme {
        // Preview content here
    }
}
