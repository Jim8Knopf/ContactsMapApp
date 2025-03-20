package jk.contacts.mapapp.ui.contactmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jk.contacts.mapapp.domain.model.Contact
import jk.contacts.mapapp.domain.model.Location
import jk.contacts.mapapp.domain.usecase.GetContactsWithLocationUseCase
import jk.contacts.mapapp.domain.usecase.SyncContactsUseCase
import jk.contacts.mapapp.domain.usecase.UpdateContactLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactMapViewModel(
    private val getContactsWithLocationUseCase: GetContactsWithLocationUseCase,
    private val syncContactsUseCase: SyncContactsUseCase,
    private val updateContactLocationUseCase: UpdateContactLocationUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ContactMapUiState>(ContactMapUiState.Loading)
    val uiState: StateFlow<ContactMapUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            // Initial sync of contacts
            syncContactsUseCase()
            
            // Observe contacts with location
            getContactsWithLocationUseCase().collect { contacts ->
                _uiState.value = ContactMapUiState.Success(contacts)
            }
        }
    }
    
    fun syncContacts() {
        viewModelScope.launch {
            syncContactsUseCase()
        }
    }
    
    fun updateContactLocation(contactId: Long, latitude: Double, longitude: Double, address: String? = null) {
        viewModelScope.launch {
            updateContactLocationUseCase(contactId, latitude, longitude, address)
        }
    }
}

sealed class ContactMapUiState {
    object Loading : ContactMapUiState()
    data class Success(val contacts: List<Contact>) : ContactMapUiState()
    data class Error(val message: String) : ContactMapUiState()
} 