package jk.contacts.mapapp.domain.model

data class Contact(
    val id: Long,
    val name: String,
    val photoUri: String?,
    val phoneNumber: String?,
    val email: String?,
    val location: Location? = null
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String?
) 