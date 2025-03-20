package jk.contacts.mapapp.ui.contactmap

import android.Manifest
import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import jk.contacts.mapapp.domain.model.Contact
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactMapScreen(
    onContactClick: (Long) -> Unit,
    viewModel: ContactMapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Dialog state for selecting a contact to assign location
    var showContactSelectionDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var showHelpDialog by remember { mutableStateOf(false) }  // Neuer State für Hilfe-Dialog
    
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )
    
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionsState.launchMultiplePermissionRequest()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            viewModel.syncContacts()
            // Zeige den Hilfe-Dialog beim ersten Start
            showHelpDialog = true
        }
    }
    
    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showHelpDialog = true },  // Hilfe-Button
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Help"
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FloatingActionButton(
                    onClick = { viewModel.syncContacts() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FloatingActionButton(
                    onClick = { onContactClick(0) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "View Contacts"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val allPermissionsGranted = permissionsState.permissions.all { 
                it.status is PermissionStatus.Granted 
            }
            
            if (!allPermissionsGranted) {
                Text(
                    text = "Permission required to access contacts and location",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                when (uiState) {
                    is ContactMapUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is ContactMapUiState.Success -> {
                        val contacts = (uiState as ContactMapUiState.Success).contacts
                        
                        var mapInitialized by remember { mutableStateOf(false) }
                        
                        // Display the map
                        AndroidView(
                            factory = { context ->
                                MapView(context).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    setTileSource(TileSourceFactory.MAPNIK)
                                    setMultiTouchControls(true)
                                    controller.setZoom(6.0)
                                    
                                    // Add long press handler to add location to contact
                                    overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                            // Schließe alle offenen Info-Fenster bei Tap
                                            InfoWindow.closeAllInfoWindowsOn(this@apply)
                                            return true
                                        }
                                        
                                        override fun longPressHelper(p: GeoPoint): Boolean {
                                            selectedLocation = p
                                            showContactSelectionDialog = true
                                            return true
                                        }
                                    }))
                                }
                            },
                            update = { mapView ->
                                if (!mapInitialized) {
                                    setupMap(mapView, contacts, context, onContactClick)
                                    mapInitialized = true
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // Hilfe-Dialog anzeigen
                        if (showHelpDialog) {
                            AlertDialog(
                                onDismissRequest = { showHelpDialog = false },
                                title = { Text("Karten-Hilfe") },
                                text = { 
                                    Text("Um einem Kontakt einen Standort hinzuzufügen, drücken Sie lange auf die gewünschte Position auf der Karte. " +
                                         "Wählen Sie dann den Kontakt aus, dem Sie den Standort zuweisen möchten.")
                                },
                                confirmButton = {
                                    Button(onClick = { showHelpDialog = false }) {
                                        Text("Verstanden")
                                    }
                                }
                            )
                        }
                        
                        // Show contact selection dialog if longpress was detected
                        if (showContactSelectionDialog && selectedLocation != null) {
                            ContactSelectionDialog(
                                contacts = contacts,
                                onDismiss = { showContactSelectionDialog = false },
                                onContactSelected = { contact ->
                                    selectedLocation?.let { location ->
                                        viewModel.updateContactLocation(
                                            contactId = contact.id,
                                            latitude = location.latitude,
                                            longitude = location.longitude
                                        )
                                        Toast.makeText(
                                            context,
                                            "Standort wurde ${contact.name} hinzugefügt",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    showContactSelectionDialog = false
                                }
                            )
                        }
                    }
                    is ContactMapUiState.Error -> {
                        Text(
                            text = (uiState as ContactMapUiState.Error).message,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactSelectionDialog(
    contacts: List<Contact>,
    onDismiss: () -> Unit,
    onContactSelected: (Contact) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kontakt auswählen") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                items(contacts) { contact ->
                    Text(
                        text = contact.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onContactSelected(contact) }
                            .padding(vertical = 8.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Schließen")
                Text("Abbrechen")
            }
        }
    )
}

private fun setupMap(
    mapView: MapView,
    contacts: List<Contact>,
    context: Context,
    onContactClick: (Long) -> Unit
) {
    // Clear existing markers (except the events overlay which should be at index 0)
    if (mapView.overlays.size > 1) {
        val eventsOverlay = mapView.overlays[0]
        mapView.overlays.clear()
        mapView.overlays.add(eventsOverlay)
    }
    
    // Find contacts with location data
    val contactsWithLocation = contacts.filter { it.location != null }
    
    if (contactsWithLocation.isEmpty()) {
        Toast.makeText(context, "Keine Kontakte mit Standortdaten gefunden", Toast.LENGTH_SHORT).show()
        
        // Set default location to center of the map (Germany)
        mapView.controller.setCenter(GeoPoint(51.1657, 10.4515))
        return
    }
    
    // Add markers for each contact with location
    contactsWithLocation.forEach { contact ->
        contact.location?.let { location ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(location.latitude, location.longitude)
                title = contact.name
                snippet = contact.phoneNumber ?: "Keine Telefonnummer"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            
            // Add click listener
            marker.setOnMarkerClickListener { marker, _ ->
                Toast.makeText(context, "Kontakt: ${marker.title}", Toast.LENGTH_SHORT).show()
                onContactClick(contact.id)
                true
            }
            
            mapView.overlays.add(marker)
        }
    }
    
    // Center the map on the first contact with location
    contactsWithLocation.firstOrNull()?.location?.let {
        mapView.controller.setCenter(GeoPoint(it.latitude, it.longitude))
    }
    
    // Force redraw
    mapView.invalidate()
} 