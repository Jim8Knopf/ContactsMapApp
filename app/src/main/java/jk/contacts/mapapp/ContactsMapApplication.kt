package jk.contacts.mapapp

import android.app.Application
import jk.contacts.mapapp.di.appModule
import jk.contacts.mapapp.di.dataModule
import jk.contacts.mapapp.di.domainModule
import jk.contacts.mapapp.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.osmdroid.config.Configuration
import java.io.File

class ContactsMapApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize OSMDroid configuration
        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = packageName
        osmConfig.osmdroidTileCache = File(cacheDir, "osmdroid")
        
        // Initialize Koin for dependency injection
        startKoin {
            androidLogger()
            androidContext(this@ContactsMapApplication)
            modules(listOf(
                appModule,
                dataModule,
                domainModule,
                uiModule
            ))
        }
    }
} 