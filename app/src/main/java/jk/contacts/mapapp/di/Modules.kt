package jk.contacts.mapapp.di

import androidx.room.Room
import jk.contacts.mapapp.data.ContactRepository
import jk.contacts.mapapp.data.ContactRepositoryImpl
import jk.contacts.mapapp.data.database.ContactDatabase
import jk.contacts.mapapp.data.mapper.ContactMapper
import jk.contacts.mapapp.domain.usecase.GetContactsWithLocationUseCase
import jk.contacts.mapapp.domain.usecase.SyncContactsUseCase
import jk.contacts.mapapp.domain.usecase.UpdateContactLocationUseCase
import jk.contacts.mapapp.ui.contactmap.ContactMapViewModel
import jk.contacts.mapapp.ui.contactlist.ContactListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Core app dependencies
val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            ContactDatabase::class.java,
            "contacts_database"
        ).build()
    }
    single { get<ContactDatabase>().contactDao() }
}

// Data layer dependencies
val dataModule = module {
    single { ContactMapper() }
    single<ContactRepository> { ContactRepositoryImpl(get(), get(), androidContext().contentResolver) }
}

// Domain layer dependencies
val domainModule = module {
    factory { GetContactsWithLocationUseCase(get()) }
    factory { SyncContactsUseCase(get()) }
    factory { UpdateContactLocationUseCase(get()) }
}

// UI layer dependencies
val uiModule = module {
    viewModel { ContactMapViewModel(get(), get(), get()) }
    viewModel { ContactListViewModel(get()) }
} 