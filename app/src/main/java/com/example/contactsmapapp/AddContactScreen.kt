package com.example.contactsmapapp

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.contacts.ContactsManager

@Composable
fun AddContactScreen(
    context: Context,
    onContactAdded: (Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Add New Contact")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (errorMsg.isNotEmpty()) {
            Text(text = errorMsg, modifier = Modifier.padding(vertical = 8.dp))
        }

        Button(onClick = {
            val hasWriteContacts = ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_CONTACTS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasWriteContacts) {
                errorMsg = "WRITE_CONTACTS permission not granted!"
                onContactAdded(false)
                return@Button
            }

            if (name.isBlank() || phone.isBlank()) {
                errorMsg = "Please enter both name and phone."
                return@Button
            }

            val success = ContactsManager.addDeviceContact(context, name, phone)
            if (success) {
                onContactAdded(true)
            } else {
                errorMsg = "Failed to add contact."
            }
        }) {
            Text("Save Contact")
        }
    }
}
