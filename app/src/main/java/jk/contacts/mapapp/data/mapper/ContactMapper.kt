package jk.contacts.mapapp.data.mapper

import android.database.Cursor
import android.provider.ContactsContract
import jk.contacts.mapapp.data.database.ContactEntity
import jk.contacts.mapapp.data.database.ContactWithLocation
import jk.contacts.mapapp.data.database.LocationEntity
import jk.contacts.mapapp.domain.model.Contact
import jk.contacts.mapapp.domain.model.Location

class ContactMapper {
    
    fun cursorToContactEntity(cursor: Cursor): ContactEntity? {
        if (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val photoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            
            if (idIndex >= 0 && nameIndex >= 0) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val photoUri = if (photoIndex >= 0) cursor.getString(photoIndex) else null
                
                return ContactEntity(
                    id = id,
                    name = name,
                    photoUri = photoUri,
                    phoneNumber = null, // Will be filled separately
                    email = null // Will be filled separately
                )
            }
        }
        return null
    }
    
    fun contactWithLocationToDomain(contactWithLocation: ContactWithLocation): Contact {
        val location = if (contactWithLocation.latitude != null && contactWithLocation.longitude != null) {
            Location(
                latitude = contactWithLocation.latitude,
                longitude = contactWithLocation.longitude,
                address = contactWithLocation.address
            )
        } else null
        
        return Contact(
            id = contactWithLocation.id,
            name = contactWithLocation.name,
            photoUri = contactWithLocation.photoUri,
            phoneNumber = contactWithLocation.phoneNumber,
            email = contactWithLocation.email,
            location = location
        )
    }
    
    fun domainContactToLocationEntity(contact: Contact): LocationEntity? {
        contact.location?.let { location ->
            return LocationEntity(
                contactId = contact.id,
                latitude = location.latitude,
                longitude = location.longitude,
                address = location.address
            )
        }
        return null
    }
} 