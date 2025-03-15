package com.example.contactsmapapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.contacts.ContactsManager
import com.example.contacts.ContactInfo
import com.example.contactsmapapp.ui.theme.ContactsMapAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactsMapAppTheme {
                AppRoot()
            }
        }
    }
}

private enum class Screen {
    HOME,
    CONTACTS_LIST,
    ADD_CONTACT
}

@Composable
fun AppRoot() {
    val context = LocalContext.current

    // Track read/write permissions
    val hasReadContacts = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val hasWriteContacts = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestReadLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasReadContacts.value = granted
    }
    val requestWriteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasWriteContacts.value = granted
    }

    // Our local list of contacts
    var contactsList by remember { mutableStateOf<List<ContactInfo>>(emptyList()) }

    // Current screen
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    // Fetch or refresh contacts
    LaunchedEffect(hasReadContacts.value) {
        if (hasReadContacts.value) {
            contactsList = ContactsManager.getDeviceContacts(context)
        }
    }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                hasReadContacts = hasReadContacts.value,
                hasWriteContacts = hasWriteContacts.value,
                onRequestRead = { requestReadLauncher.launch(Manifest.permission.READ_CONTACTS) },
                onRequestWrite = { requestWriteLauncher.launch(Manifest.permission.WRITE_CONTACTS) },
                onGoContacts = { currentScreen = Screen.CONTACTS_LIST }
            )
        }
        Screen.CONTACTS_LIST -> {
            ContactsListScreen(
                contacts = contactsList,
                onRefresh = {
                    if (hasReadContacts.value) {
                        contactsList = ContactsManager.getDeviceContacts(context)
                    }
                },
                onAddNewContact = {
                    currentScreen = Screen.ADD_CONTACT
                }
            )
        }
        Screen.ADD_CONTACT -> {
            AddContactScreen(
                context = context,
                onContactAdded = { success ->
                    if (success && hasReadContacts.value) {
                        contactsList = ContactsManager.getDeviceContacts(context)
                    }
                    currentScreen = Screen.CONTACTS_LIST
                }
            )
        }
    }
}
