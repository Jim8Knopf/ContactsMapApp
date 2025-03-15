package com.example.contacts

import android.content.ContentProviderOperation
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract

data class ContactInfo(
    val displayName: String,
    val phoneNumber: String?
)

object CM {

    /**
     * Retrieves contacts from the device using the system's ContentResolver.
     * Host app must handle READ_CONTACTS permission at runtime.
     */
    fun getDeviceContacts(context: Context): List<ContactInfo> {
        val contactsList = mutableListOf<ContactInfo>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: "Unknown"
                val number = it.getString(numberIndex)
                contactsList.add(ContactInfo(name, number))
            }
        }

        return contactsList
    }

    /**
     * Adds a new contact to the device's contact list, with the given name and phone number.
     * Host app must request and have WRITE_CONTACTS permission at runtime.
     */
    fun addDeviceContact(context: Context, displayName: String, phoneNumber: String): Boolean {
        return try {
            val operations = ArrayList<ContentProviderOperation>()

            // Insert raw contact with an empty account
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            // Insert display name
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        displayName
                    )
                    .build()
            )

            // Insert phone number
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )

            // Apply batch of insert operations
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
