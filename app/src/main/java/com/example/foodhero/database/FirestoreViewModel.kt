package com.example.foodhero.database
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodhero.global.ServerResult
import com.example.foodhero.global.logMessage
import com.example.foodhero.struct.*
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.tasks.await

class FirestoreViewModel:ViewModel() {
    var serverDetails = ArrayList<ServerDetails>()
    var firebaseRepository = FirestoreRepository()
    var savedRestaurantMenuItems : MutableLiveData<List<MenuItem>?> = MutableLiveData()
    var savedRestaurants : MutableLiveData<List<Restaurant>?> = MutableLiveData()

    private fun clearServerDetails(){
        serverDetails.clear()
    }

    suspend fun saveRestaurantToFirebase(pos:Int,restaurant:Restaurant,imageUri: Uri,):Boolean{
        clearServerDetails()
        saveRestaurantInfo(pos,restaurant)
        saveRestaurantLoggoToFirebase(pos,imageUri,restaurant.loggoDownloadUrl!!)
        return serverDetails.isEmpty()
    }

    private suspend fun saveRestaurantInfo(pos:Int,restaurant:Restaurant){
        firebaseRepository.saveRestaurant(restaurant).addOnCompleteListener { task->
            if(!task.isSuccessful){
                serverDetails.add(ServerDetails(pos,task.exception.toString(), ServerResult.UPLOAD_ERROR))
            }
        }.await()
    }

    suspend fun saveMenuToFirebase(pos:Int,menu: Menu):Boolean{
        clearServerDetails()
        for((i, item) in menu.menuItems.withIndex()){
            val uri = menu.imageUri[i]
            saveMenuItemToFirebase(pos,item,menu.restaurantId)
            saveMenuItemLoggoToFirebase(pos,uri,item.loggoDownloadUrl!!)
        }
        return serverDetails.isEmpty()
    }

    suspend fun saveDrinkListToFirebase(pos:Int,drinkList: DrinkList):Boolean{
        clearServerDetails()
        for((i, item) in drinkList.drinkItems.withIndex()){
            saveDrinkItemToFirebase(pos,item,drinkList.restaurantId)
       }
        return serverDetails.isEmpty()
    }

    private suspend fun saveMenuItemToFirebase(pos:Int,menuItem: MenuItem,restaurantId:String){
        firebaseRepository.saveMenuItem(restaurantId,menuItem).addOnCompleteListener { task->
            if(!task.isSuccessful){
                serverDetails.add(ServerDetails(pos,task.exception.toString(), ServerResult.UPLOAD_ERROR))
            }
        }.await()
    }

    private suspend fun saveDrinkItemToFirebase(pos:Int,drinkItem: DrinkItem,restaurantId:String){
        firebaseRepository.saveDrinkItem(restaurantId,drinkItem).addOnCompleteListener { task->
            if(!task.isSuccessful){
                serverDetails.add(ServerDetails(pos,task.exception.toString(), ServerResult.UPLOAD_ERROR))
            }
        }.await()
    }

    private suspend fun saveRestaurantLoggoToFirebase(pos:Int, imageUri: Uri, downloadUrl:String){
        firebaseRepository.saveRestaurantLoggo(imageUri, downloadUrl).addOnCompleteListener { task->
            if(!task.isSuccessful){
                serverDetails.add(ServerDetails(pos,task.exception.toString(), ServerResult.UPLOAD_ERROR))
            }
        }.await()
    }

    private suspend fun saveMenuItemLoggoToFirebase(pos:Int, imageUri: Uri, downloadUrl:String){
        firebaseRepository.saveMenuItemLoggo(imageUri, downloadUrl).addOnCompleteListener { task->
            if(!task.isSuccessful){
                serverDetails.add(ServerDetails(pos,task.exception.toString(), ServerResult.UPLOAD_ERROR))
            }
        }.await()

    }

    /*fun getMenuItems(restaurantId:String): LiveData<List<MenuItem>?> {
        firebaseRepository.getSavedMenuItems(restaurantId).addSnapshotListener EventListener@{ value, e ->
            if (e != null) {
                savedRestaurantMenuItems.value = null
                return@EventListener
            }

            val savedMenuItemList : MutableList<MenuItem> = mutableListOf()
            for (doc in value!!.documentChanges) {
                val menuItem = doc.document.toObject(MenuItem::class.java)
                if(doc.type == DocumentChange.Type.REMOVED){
                //callbackRemove(menuItem)
                // }
                }
                else{savedMenuItemList.add(menuItem)}
            }
            savedRestaurantMenuItems.value = savedMenuItemList
        }

        return savedRestaurantMenuItems
    }*/

    fun getMenuItems(restaurantId:String){
        firebaseRepository.getSavedMenuItems(restaurantId).get().addOnCompleteListener{it->
            if(it.isSuccessful){
                for(doc in it.result){
                    //menu.add(doc.toObject(MenuItem::class.java))
                    logMessage(doc.toObject(MenuItem::class.java).toString())
                }
            }
        }
    }

    fun getDrinkList(restaurantId:String){
        firebaseRepository.getSavedDrinkItems(restaurantId).get().addOnCompleteListener{it->
            if(it.isSuccessful){
                for(doc in it.result){
                    //drinkList.add(doc.toObject(DrinkItem::class.java))
                    logMessage(doc.toObject(DrinkItem::class.java).toString())
                }
            }
        }
    }

    fun getRestaurants(): LiveData<List<Restaurant>?> {
        firebaseRepository.getSavedRestaurants().addSnapshotListener EventListener@{ value, e ->
            if(e != null) {
                //printToTerminal("Listen failed ${e.message.toString()}")
                savedRestaurants.value = null
                return@EventListener
            }
            val savedRestaurantsList : MutableList<Restaurant> = mutableListOf()
            for (doc in value!!.documentChanges) {
                if(doc.type != DocumentChange.Type.REMOVED){
                    val restaurant = doc.document.toObject(Restaurant::class.java)
                    savedRestaurantsList.add(restaurant)
                }
            }
            savedRestaurants.value = savedRestaurantsList
        }

        return savedRestaurants
    }

    fun deleteMenuFromFirebase(restaurantId:String,docId:String){
        firebaseRepository.deleteMenuItem(restaurantId,docId).addOnFailureListener {
            //printToTerminal("Failed to delete Restaurant Menu Item")
        }
    }

    fun deleteMenuItemFromFirebase(restaurantId:String,docId:String){
        firebaseRepository.deleteMenuItem(restaurantId,docId).addOnFailureListener {
            //printToTerminal("Failed to delete Restaurant Menu Item")
        }
    }

    fun deleteRestaurantFromFirebase(restaurantId:String){
        firebaseRepository.deleteRestaurant(restaurantId).addOnFailureListener {
            //printToTerminal("Failed to delete Restaurant")
        }
    }

    fun deleteMenuItemLoggoFromFirebase(downloadUrl:String){
        firebaseRepository.deleteMenuItemLoggo(downloadUrl).addOnFailureListener {
            //printToTerminal("Failed to delete Restaurant Menu Item Loggo")
        }
    }

    fun deleteRestaurantLoggoFromFirebase(downloadUrl:String){
        firebaseRepository.deleteRestaurantLoggo(downloadUrl).addOnFailureListener {
            //printToTerminal("Failed to delete Restaurant Loggo")
        }
    }
}