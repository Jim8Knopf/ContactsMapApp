package jk.contacts.mapapp.domain.usecase

import jk.contacts.mapapp.data.ContactRepository

class SyncContactsUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke() {
        contactRepository.syncContacts()
    }
} 