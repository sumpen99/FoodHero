package com.example.foodhero.struct

class User (
    val email:String?=null,
    val phoneNumber:String?=null,
    val adress:String?=null,
    val socialSecurityNumber:String?=null,
    val password:String?=null, ):java.io.Serializable
         {
             override fun toString(): String {
                 return "\n" +
                         "Email: $email\n" +
                         "Phonenumber: $phoneNumber\n" +
                         "Adress: $adress\n" +
                         "Password: $password\n"}
}