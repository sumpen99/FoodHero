package com.example.foodhero.database
import android.net.Uri
import com.example.foodhero.global.*
import com.example.foodhero.struct.DrinkItem
import com.example.foodhero.struct.MenuItem
import com.example.foodhero.struct.Restaurant
import com.example.foodhero.struct.User
import com.firebase.geofire.GeoFireUtils

import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class FirestoreRepository {
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val firestoreStorage = Firebase.storage.reference

    fun saveMenuItem(restaurantId:String,menuItem: MenuItem): Task<Void> {
        val documentReference = firestoreDB.collection(RESTAURANT_COLLECTION)
            .document(restaurantId)
            .collection(MENU_COLLECTION)
            .document(menuItem.menuItemId!!)
        return documentReference.set(menuItem)
    }

    fun saveDrinkItem(restaurantId:String,drinkItem: DrinkItem): Task<Void> {
        val documentReference = firestoreDB.collection(RESTAURANT_COLLECTION)
            .document(restaurantId)
            .collection(DRINK_COLLECTION)
            .document(drinkItem.drinkItemId!!)
        return documentReference.set(drinkItem)
    }

    fun saveRestaurant(restaurant: Restaurant): Task<Void> {
        return firestoreDB.collection(RESTAURANT_COLLECTION)
            .document(restaurant.restaurantId!!)
            .set(restaurant)
    }

    fun saveUser(user: User): Task<Void> {
        return firestoreDB.collection(USER_COLLECTION)
            .document(user.email!!)
            .set(user)
    }

    fun saveRestaurantLoggo(imageUri: Uri, downloadUrl:String): UploadTask {
        val path = "$RESTAURANT_LOGGO_PATH${downloadUrl}"
        val storageRef = firestoreStorage.child(path)
        return storageRef.putFile(imageUri)
    }

    fun saveMenuItemLoggo(imageUri: Uri, downloadUrl:String): UploadTask {
        val path = "$MENUITEM_LOGGO_PATH${downloadUrl}"
        val storageRef = firestoreStorage.child(path)
        return storageRef.putFile(imageUri)
    }

    fun saveRestaurantGeo(restaurant:Restaurant):Task<Void>{
        val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(restaurant.lat!!,restaurant.lon!!))
        val updates: MutableMap<String, Any> = mutableMapOf(
            "geohash" to hash,
        )
        return firestoreDB.collection(RESTAURANT_COLLECTION).document(restaurant.restaurantId!!).update(updates)
    }

    fun getSavedMenuItems(restaurantId:String): CollectionReference {
        val path = "$RESTAURANT_COLLECTION/${restaurantId}/$MENU_COLLECTION"
        return firestoreDB.collection(path)
    }

    fun getSavedDrinkItems(restaurantId:String): CollectionReference {
        val path = "$RESTAURANT_COLLECTION/${restaurantId}/$DRINK_COLLECTION"
        return firestoreDB.collection(path)
    }

    fun getSavedRestaurants(): CollectionReference {
        return firestoreDB.collection(RESTAURANT_COLLECTION)
    }

    fun getRestaurantLoggoReference(downloadUrl:String?): StorageReference {
        val path = "$RESTAURANT_LOGGO_PATH${downloadUrl}"
        return firestoreStorage.child(path)
    }

    fun getMenuItemLoggoReference(downloadUrl:String?): StorageReference {
        val path = "$MENUITEM_LOGGO_PATH${downloadUrl}"
        return firestoreStorage.child(path)
    }

    fun deleteMenuItem(restaurantId:String,menuItemId: String): Task<Void> {
        val path = "$RESTAURANT_COLLECTION/${restaurantId}/$MENU_COLLECTION"
        return firestoreDB.collection(path).document(menuItemId).delete()
    }

    fun deleteDrinkItem(restaurantId:String,drinkItemId: String): Task<Void> {
        val path = "$RESTAURANT_COLLECTION/${restaurantId}/$DRINK_COLLECTION"
        return firestoreDB.collection(path).document(drinkItemId).delete()
    }

    fun deleteRestaurant(restaurantId: String): Task<Void> {
        return firestoreDB.collection(RESTAURANT_COLLECTION).document(restaurantId).delete()
    }

    fun deleteMenuItemLoggo(downloadUrl:String): Task<Void> {
        val path = "$MENUITEM_LOGGO_PATH${downloadUrl}"
        val storageRef = firestoreStorage.child(path)
        return storageRef.delete()
    }

    fun deleteRestaurantLoggo(downloadUrl:String): Task<Void> {
        val path = "$RESTAURANT_LOGGO_PATH${downloadUrl}"
        val storageRef = firestoreStorage.child(path)
        return storageRef.delete()
    }

}