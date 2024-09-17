package com.example.workout_app

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("No")
                }
            }
        )
    }
}