package com.example.modaktestone.navigation.util

import com.example.modaktestone.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyRegion {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var currentUserUid: String? = null
    var region: String? = null

    val test : String? =null
    init {
        region = {
            firestore?.collection("users")?.document(auth?.currentUser?.uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if(documentSnapshot==null)return@addSnapshotListener
                    var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                    region = regionDTO?.region
                }
        }.toString()
    }


//    fun getRegion(): String? {
//        var region : String? = null
//        firestore?.collection("users")?.document(auth?.currentUser?.uid!!)
//            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
//                if(documentSnapshot==null)return@addSnapshotListener
//                var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
//                region = regionDTO?.region
//            }
//
//        return region
//    }
}