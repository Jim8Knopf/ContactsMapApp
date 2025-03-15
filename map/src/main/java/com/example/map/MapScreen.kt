package com.example.map

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class MapContact(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

@Composable
fun MapScreen(
    context: Context,
    contactPoints: List<MapContact>
) {
    // Initialize OSMDroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(4.0)
                    controller.setCenter(GeoPoint(0.0, 0.0))

                    contactPoints.forEach { mapContact ->
                        val marker = Marker(this).apply {
                            position = GeoPoint(mapContact.latitude, mapContact.longitude)
                            title = mapContact.name
                        }
                        overlays.add(marker)
                    }
                }
            },
            update = { mapView ->
                Log.d("MapScreen", "Map updated with ${contactPoints.size} contacts.")
            }
        )
    }
}
