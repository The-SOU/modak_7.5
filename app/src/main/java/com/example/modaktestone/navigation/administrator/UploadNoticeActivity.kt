package com.example.modaktestone.navigation.administrator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.modaktestone.databinding.ActivityUploadNoticeBinding
import com.example.modaktestone.navigation.model.InquiryDTO
import com.example.modaktestone.navigation.model.NoticeDTO
import com.google.firebase.firestore.FirebaseFirestore

class UploadNoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadNoticeBinding

    var firestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadNoticeBinding.inflate(layoutInflater)
        val view = binding.root

        firestore = FirebaseFirestore.getInstance()

        binding.uploadBtn.setOnClickListener {
            var noticeDTO = NoticeDTO()

            noticeDTO.title = binding.uploadTitle.text.toString()
            noticeDTO.explain = binding.uploadExplain.text.toString()
            noticeDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("notices")?.add(noticeDTO)?.addOnSuccessListener { documentReference ->
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