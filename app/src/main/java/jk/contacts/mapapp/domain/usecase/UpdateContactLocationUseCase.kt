package jk.contacts.mapapp.domain.usecase

import jk.contacts.mapapp.data.ContactRepository
import jk.contacts.mapapp.domain.model.Location

class UpdateContactLocationUseCase(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contactId: Long, latitude: Double, longitude: Double, address: String? = null) {
        val location = Location(
            latitude = latitude,
            longitude = longitude,
            address = address
        )
        contactRepository.updateContactLocation(contactId, location)
    }
} 