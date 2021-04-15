package com.example.modaktestone.navigation.util

import com.example.modaktestone.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class FcmPush {
    var JSON : MediaType = MediaType.parse("application/json; charset=utf-8")
    var url = "https://fcm.googleapis.com/fcm/send"
    var severKey =
        "AAAAgNA-3gc:APA91bFjkmlOivKAK_mGGjrjZUjmGX_oLtRzJzpx6QSCbuDm8NUfDiGb1BOzhwg6k8NW-IIdHcFOr0wZM9rg-kWDZlu0m4AqeIezUhW_DijAb7s-maYLfofoKymQqzyXhAzRDqsi5MSW"
    var gson: Gson? = null
    var okHttpClient: OkHttpClient? = null

    companion object {
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String?, title: String?, message: String?) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid!!).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var token = task.result?.get("pushtoken").toString()

                    var pushDTO = PushDTO()
                    pushDTO.to = token
                    pushDTO.notification.title = title
                    pushDTO.notification.body = message

                    var body = pushDTO.toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())


                    var request =
                        okhttp3.Request.Builder().addHeader("Content-type", "application/json")
                            .addHeader("Authorization", "key="+severKey).url(url).post(body)
                            .build()

                    okHttpClient?.newCall(request)?.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            println(response?.body?.string())

                        }

                    })
                }
            }
    }

}