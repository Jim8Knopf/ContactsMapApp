package com.example.contactsmapapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(
    hasReadContacts: Boolean,
    hasWriteContacts: Boolean,
    onRequestRead: () -> Unit,
    onRequestWrite: () -> Unit,
    onGoContacts: () -> Unit
) {
    Column {
        Text("Welcome to ContactsMapApp")

        if (!hasReadContacts) {
            Button(onClick = { onRequestRead() }) {
                Text("Request READ_CONTACTS")
            }
        } else {
            Text("READ_CONTACTS granted")
        }

        if (!hasWriteContacts) {
            Button(onClick = { onRequestWrite() }) {
                Text("Request WRITE_CONTACTS")
            }
        } else {
            Text("WRITE_CONTACTS granted")
        }

        Button(onClick = { onGoContacts() }) {
            Text("Go to Contacts List")
        }

        // If you want to link to a map screen, you could add a button here:
        // Button(onClick = { /* navigate to map screen */ }) {
        //     Text("Go to Map")
        // }
    }
}
