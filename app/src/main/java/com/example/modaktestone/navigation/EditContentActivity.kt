package com.example.modaktestone.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ActivityAddContentBinding
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.kakao.sdk.common.KakaoSdk.init
import org.koin.android.ext.android.bind
import java.text.SimpleDateFormat
import java.util.*

class EditContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContentBinding


    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var anonymityDTO = ContentDTO()

    var originalTitle: String? = null
    var originalExplain: String? = null
    var contentUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //인텐트값 받기
        originalTitle = intent.getStringExtra("editTitle")
        originalExplain = intent.getStringExtra("editExplain")
        binding.addcontentEdittextTitle.setText(originalTitle)
        binding.addcontentEdittextExplain.setText(originalExplain)
        contentUid = intent.getStringExtra("contentUid")

        //카메라 버튼 클릭했을 때 사진 선택하는 곳으로 넘어가기.
        binding.addcontentImageviewCamera.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        binding.addcontentImageviewImage.visibility = View.INVISIBLE

        binding.addcontentBtnUpload.setOnClickListener {

            contentEdit(anonymityDTO)

            //username, region 얻기.

        }

        //익명 버튼 클릭시
        binding.addcontentLinearAnonymity.setOnClickListener {
            if (anonymityDTO.anonymity.containsKey(auth?.currentUser?.uid)) {
                anonymityDTO.anonymity.remove(auth?.currentUser?.uid)
                binding.addcontentImageviewAnonymity.setImageResource(R.drawable.ic_unanonymity)
                binding.addcontentTvAnonymity.setTextColor(Color.parseColor("#919191"))
                binding.addcontentTvAnonymity.setTypeface(null, Typeface.NORMAL)
                println("anonymity delete complete")
            } else {
                anonymityDTO.anonymity[auth?.currentUser?.uid!!] = true
                binding.addcontentImageviewAnonymity.setImageResource(R.drawable.ic_anonymity)
                binding.addcontentTvAnonymity.setTextColor(Color.BLACK)
                binding.addcontentTvAnonymity.setTypeface(null, Typeface.BOLD)
                println("anonymity add complete")
            }
        }

        //키보드 숨기기
        binding.layout.setOnClickListener {
            hideKeyboard()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                binding.addcontentImageviewImage.setImageURI(photoUri)
                binding.addcontentImageviewImage.visibility = View.VISIBLE
            } else {
                finish()
            }
        }
    }

    // ----- 펑션 모음 -----

    fun contentEdit(anonymity: ContentDTO) {
        println(anonymity.anonymity.toString())
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        var tsDoc = firestore?.collection("contents")?.document(contentUid!!)
        firestore?.runTransaction { transaction ->

            if (photoUri != null) {
                //image uri가 존재할 때
                storageRef?.putFile(photoUri!!)
                    ?.continueWith { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                        return@continueWith storageRef.downloadUrl
                    }?.addOnCompleteListener { uri ->

                        var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                        contentDTO?.imageUrl = uri.toString()

                        if (anonymity.anonymity.containsKey(auth?.currentUser?.uid!!)) {
                            contentDTO!!.anonymity[auth?.currentUser?.uid!!] = true
                        }else{
                            contentDTO!!.anonymity.remove(auth?.currentUser?.uid!!)
                        }

                        contentDTO?.title = binding.addcontentEdittextTitle.text.toString()
                        contentDTO?.explain = binding.addcontentEdittextExplain.text.toString()

                        transaction.set(tsDoc, contentDTO!!)
                    }

            } else {
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (anonymity.anonymity.containsKey(auth?.currentUser?.uid!!)) {
                    contentDTO!!.anonymity[auth?.currentUser?.uid!!] = true
                }else{
                    contentDTO!!.anonymity.remove(auth?.currentUser?.uid!!)
                }

                contentDTO?.title = binding.addcontentEdittextTitle.text.toString()
                contentDTO?.explain = binding.addcontentEdittextExplain.text.toString()

                transaction.set(tsDoc, contentDTO!!)
                return@runTransaction
            }

        }
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