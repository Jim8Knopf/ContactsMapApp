package jk.contacts.mapapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jk.contacts.mapapp.ui.contactlist.ContactListScreen
import jk.contacts.mapapp.ui.contactmap.ContactMapScreen
import jk.contacts.mapapp.ui.theme.JKContactsMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JKContactsMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "map",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("map") {
                ContactMapScreen(
                    onContactClick = { contactId ->
                        navController.navigate("list")
                    }
                )
            }
            composable("list") {
                ContactListScreen(
                    onMapClick = {
                        navController.navigate("map") {
                            popUpTo("map") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
} 