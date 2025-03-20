package jk.contacts.mapapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long
    
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>
    
    @Transaction
    @Query("SELECT c.*, l.latitude, l.longitude, l.address FROM contacts c LEFT JOIN locations l ON c.id = l.contactId ORDER BY c.name ASC")
    fun getContactsWithLocation(): Flow<List<ContactWithLocation>>
    
    @Query("SELECT * FROM contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: Long): ContactEntity?
    
    @Query("SELECT * FROM locations WHERE contactId = :contactId")
    suspend fun getLocationForContact(contactId: Long): LocationEntity?
    
    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()
}

data class ContactWithLocation(
    val id: Long,
    val name: String,
    val photoUri: String?,
    val phoneNumber: String?,
    val email: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?
) 