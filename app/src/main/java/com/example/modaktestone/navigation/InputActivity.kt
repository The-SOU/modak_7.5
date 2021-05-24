package com.example.modaktestone.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ActivityHomepageViewBinding
import com.example.modaktestone.databinding.ActivityInputBinding
import com.example.modaktestone.navigation.model.WelfareCenterDTO
import com.google.firebase.firestore.FirebaseFirestore

class InputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputBinding

    var firestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        val view = binding.root

        firestore = FirebaseFirestore.getInstance()

        binding.inputBtn.setOnClickListener {
            var welfareDTO = WelfareCenterDTO()

            welfareDTO.name = binding.inputName.text.toString()
            welfareDTO.region = binding.inputRegion.text.toString()
            welfareDTO.url = binding.inputUrl.text.toString()


            firestore?.collection("welfareCenters")?.add(welfareDTO)?.addOnSuccessListener { documentReference ->
                Log.d(
                    "TAG",
                    "DocumentSnapshot written with ID: ${documentReference.id}"
                )
                Toast.makeText(this, "입력완료", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
        }


        setContentView(view)


    }
}