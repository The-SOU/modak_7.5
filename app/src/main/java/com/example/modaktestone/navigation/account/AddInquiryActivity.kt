package com.example.modaktestone.navigation.account

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.example.modaktestone.databinding.ActivityAddInquiryBinding
import com.example.modaktestone.navigation.model.InquiryDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddInquiryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddInquiryBinding

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInquiryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth?.currentUser?.uid

        //액션바 설정
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

        //완료 버튼 클릭 시.
        binding.addinquiryBtnUpload.setOnClickListener {
            inquiryUpload()
        }

        //키보드 숨기기
        binding.layout.setOnClickListener {
            hideKeyboard()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun inquiryUpload() {
        var inquiryDTO = InquiryDTO()

        inquiryDTO.title = binding.addinquiryEdittextTitle.text.toString()
        inquiryDTO.explain = binding.addcontentEdittextExplain.text.toString()
        inquiryDTO.inquire = uid
        inquiryDTO.timestamp = System.currentTimeMillis()

        firestore?.collection("inquiries")?.add(inquiryDTO)
            ?.addOnSuccessListener { documentReference ->
                Log.d(
                    "TAG",
                    "DocumentSnapshot written with ID: ${documentReference.id}")
            }?.addOnFailureListener { e ->
            Log.w("TAG", "Error adding document", e)
        }

        setResult(Activity.RESULT_OK)

        finish()

    }

    fun hideKeyboard(){
        val view = this.currentFocus
        if(view != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}