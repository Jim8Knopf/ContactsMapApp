package com.example.contactsmapapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contacts.ContactInfo

@Composable
fun ContactsListScreen(
    contacts: List<ContactInfo>,
    onRefresh: () -> Unit,
    onAddNewContact: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Your Contacts", modifier = Modifier.padding(bottom = 16.dp))

        Button(onClick = { onRefresh() }) {
            Text("Refresh Contacts")
        }

        Spacer(Modifier.height(16.dp))

        contacts.forEach { contact ->
            Text(
                text = "Name: ${contact.displayName}, Phone: ${contact.phoneNumber}",
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onAddNewContact) {
            Text("Add New Contact")
        }
    }
}
