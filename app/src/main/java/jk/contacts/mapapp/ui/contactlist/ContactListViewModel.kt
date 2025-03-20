package jk.contacts.mapapp.ui.contactlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jk.contacts.mapapp.domain.model.Contact
import jk.contacts.mapapp.domain.usecase.GetContactsWithLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactListViewModel(
    private val getContactsWithLocationUseCase: GetContactsWithLocationUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ContactListUiState>(ContactListUiState.Loading)
    val uiState: StateFlow<ContactListUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            getContactsWithLocationUseCase().collect { contacts ->
                _uiState.value = ContactListUiState.Success(contacts)
            }
        }
    }
}

sealed class ContactListUiState {
    object Loading : ContactListUiState()
    data class Success(val contacts: List<Contact>) : ContactListUiState()
    data class Error(val message: String) : ContactListUiState()
} 