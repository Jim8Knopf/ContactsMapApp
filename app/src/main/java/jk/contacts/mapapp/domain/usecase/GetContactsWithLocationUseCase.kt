package jk.contacts.mapapp.domain.usecase

import jk.contacts.mapapp.data.ContactRepository
import jk.contacts.mapapp.domain.model.Contact
import kotlinx.coroutines.flow.Flow

class GetContactsWithLocationUseCase(private val contactRepository: ContactRepository) {
    operator fun invoke(): Flow<List<Contact>> {
        return contactRepository.getContactsWithLocation()
    }
} 