package jk.contacts.mapapp.data

import android.content.ContentResolver
import android.provider.ContactsContract
import jk.contacts.mapapp.data.database.ContactDao
import jk.contacts.mapapp.data.database.ContactEntity
import jk.contacts.mapapp.data.mapper.ContactMapper
import jk.contacts.mapapp.domain.model.Contact
import jk.contacts.mapapp.domain.model.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface ContactRepository {
    fun getContactsWithLocation(): Flow<List<Contact>>
    suspend fun syncContacts()
    suspend fun updateContactLocation(contactId: Long, location: Location)
}

class ContactRepositoryImpl(
    private val contactDao: ContactDao,
    private val contactMapper: ContactMapper,
    private val contentResolver: ContentResolver
) : ContactRepository {
    
    override fun getContactsWithLocation(): Flow<List<Contact>> {
        return contactDao.getContactsWithLocation().map { contacts ->
            contacts.map { contactMapper.contactWithLocationToDomain(it) }
        }
    }
    
    override suspend fun syncContacts() = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI
        )
        
        val contactEntities = mutableListOf<ContactEntity>()
        
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val photoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
                
                if (idIndex >= 0 && nameIndex >= 0) {
                    val id = cursor.getLong(idIndex)
                    val name = cursor.getString(nameIndex)
                    val photoUri = if (photoIndex >= 0) cursor.getString(photoIndex) else null
                    
                    // Get phone number
                    var phoneNumber: String? = null
                    contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id.toString()),
                        null
                    )?.use { phoneCursor ->
                        if (phoneCursor.moveToFirst()) {
                            val phoneNumberIndex = phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                            if (phoneNumberIndex >= 0) {
                                phoneNumber = phoneCursor.getString(phoneNumberIndex)
                            }
                        }
                    }
                    
                    // Get email
                    var email: String? = null
                    contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(id.toString()),
                        null
                    )?.use { emailCursor ->
                        if (emailCursor.moveToFirst()) {
                            val emailIndex = emailCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Email.ADDRESS
                            )
                            if (emailIndex >= 0) {
                                email = emailCursor.getString(emailIndex)
                            }
                        }
                    }
                    
                    contactEntities.add(
                        ContactEntity(
                            id = id,
                            name = name,
                            photoUri = photoUri,
                            phoneNumber = phoneNumber,
                            email = email
                        )
                    )
                }
            }
        }
        
        if (contactEntities.isNotEmpty()) {
            contactDao.insertContacts(contactEntities)
        }
    }
    
    override suspend fun updateContactLocation(contactId: Long, location: Location) {
        withContext(Dispatchers.IO) {
            val contact = contactDao.getContactById(contactId) ?: return@withContext
            val domainContact = Contact(
                id = contact.id,
                name = contact.name,
                photoUri = contact.photoUri,
                phoneNumber = contact.phoneNumber,
                email = contact.email,
                location = location
            )
            
            contactMapper.domainContactToLocationEntity(domainContact)?.let {
                contactDao.insertLocation(it)
            }
        }
    }
} 