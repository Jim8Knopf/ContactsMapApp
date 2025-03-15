package com.example.contacts

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.util.Log

data class ContactInfo(val name: String, val number: String)

object ContactsManager {

    private const val TAG = "ContactsManager"

    /**
     * Retrieves contacts from the device using the system's ContentResolver.
     * Host app must handle READ_CONTACTS permission at runtime.
     *
     * @param context The application context.
     * @return A list of ContactInfo objects, each containing a contact's name and number.
     */
    fun getDeviceContacts(context: Context): List<ContactInfo> {
        val contactsList = mutableListOf<ContactInfo>()
        val contentResolver: ContentResolver = context.contentResolver

        // Define the columns to retrieve from the Contacts Provider.
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        //Using use for auto close the cursor.
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            // Get the column indexes for name and number.
            val nameIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            // Iterate through the cursor, extracting contact data.
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex) ?: "Unknown"
                val number = cursor.getString(numberIndex) ?: "" // Handle null number
                contactsList.add(ContactInfo(name, number))
            }
        }
        // return empty list when there is no contacts.
        return contactsList
    }

    /**
     * Adds a new contact to the device's contact list.
     *
     * Host app must request and have WRITE_CONTACTS permission at runtime.
     *
     * @param context The application context.
     * @param displayName The display name of the new contact.
     * @param phoneNumber The phone number of the new contact.
     * @return True if the contact was successfully added, false otherwise.
     */
    fun addDeviceContact(context: Context, displayName: String, phoneNumber: String): Boolean {
        // Check for empty inputs.
        if (displayName.isBlank() || phoneNumber.isBlank()) {
            Log.e(TAG, "Error: Display name or phone number cannot be empty.")
            return false
        }

        val operations = ArrayList<ContentProviderOperation>()

        // 1. Insert RawContact (for grouping data).
        val rawContactInsertIndex = operations.size
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        // 2. Insert StructuredName (display name).
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    displayName
                ) // Use DISPLAY_NAME instead of GIVEN_NAME
                .build()
        )

        // 3. Insert Phone number.
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
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

        return try {
            // 4. Execute batch operation.
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
            Log.d(TAG, "Contact '$displayName' added successfully.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding contact '$displayName': ${e.message}")
            e.printStackTrace()
            false
        }
    }
}