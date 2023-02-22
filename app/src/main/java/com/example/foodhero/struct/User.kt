package com.example.foodhero.struct

class User(
    val email: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val postalCode: String? = null,
    val city: String? = null
) : java.io.Serializable {

    override fun toString(): String {
        return "\n" +
                "Email: $email\n" +
                "Name: $name\n" +
                "Phonenumber: $phoneNumber\n" +
                "Postal Code: $postalCode\n" +
                "City: $city\n"
    }
}
