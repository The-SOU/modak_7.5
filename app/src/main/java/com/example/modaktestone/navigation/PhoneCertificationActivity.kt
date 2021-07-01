package com.example.modaktestone.navigation

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.modaktestone.MainActivity
import com.example.modaktestone.databinding.ActivityHomepageViewBinding
import com.example.modaktestone.databinding.ActivityPhoneCertificationBinding
import com.example.modaktestone.navigation.model.UserDTO
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.android.bind
import java.util.concurrent.TimeUnit

class PhoneCertificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneCertificationBinding

    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    //코드보내기 실패하면 재전송
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    private val TAG = "MAIN_TAG"

    //progress dialog
    private lateinit var processDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneCertificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //키보드 사라지기
        binding.layout.setOnClickListener {
            hideKeyboard()
        }


        //초기화
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid




        binding.phoneLayout1.visibility = View.VISIBLE
        binding.phoneLayout2.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        processDialog = ProgressDialog(this)
        processDialog.setTitle("잠시 기다려주세요")
        processDialog.setCanceledOnTouchOutside(false)

        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: ")
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                processDialog.dismiss()
                Log.d(TAG, "onVerificationFailed: ${e.message}")
                Toast.makeText(this@PhoneCertificationActivity, "${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent: $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                processDialog.dismiss()

                Log.d(TAG, "onCodeSent: $verificationId")

                //첫번째 레이아웃 숨기고 두번째 레이아웃 나타내기
                binding.phoneLayout1.visibility = View.GONE
                binding.phoneLayout2.visibility = View.VISIBLE
                Toast.makeText(
                    this@PhoneCertificationActivity,
                    "Verification code sent...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //전송하기 클릭
        binding.phoneBtnSend.setOnClickListener {
            //폰번호 넣기
            val phone = "+82${binding.phoneEditPhonenumber.text.toString().trim()}"
            //validate phone number
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this@PhoneCertificationActivity, "휴대폰 번호를 넣어주세요", Toast.LENGTH_SHORT)
                    .show()
            } else {
                startPhoneNumberVerification(phone)
            }
        }

        //재전송 클릭
        binding.phoneBtnResend.setOnClickListener {
            val phone = "+82${binding.phoneEditPhonenumber.text.toString().trim()}"
            //validate phone number
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this@PhoneCertificationActivity, "휴대폰 번호를 넣어주세요", Toast.LENGTH_SHORT)
                    .show()
            } else {
                resendVerificationCode(phone, forceResendingToken)
            }
        }

        binding.phoneBtnCertificate.setOnClickListener {
            //input verification code
            val code = binding.phoneEditCertificate.text.toString().trim()
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this@PhoneCertificationActivity, "인증번호를 넣어주세요", Toast.LENGTH_SHORT)
                    .show()
            } else {
                verifyingPhoneNumberWithCode(mVerificationId, code)
            }
        }
    }

    private fun startPhoneNumberVerification(phone: String) {
        Log.d(TAG, "startPhoneNumberVerification: $phone")
        processDialog.setMessage("Verifying Phone Number...")
        processDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallBacks!!).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(
        phone: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        processDialog.setMessage("Resending Code...")
        processDialog.show()

        Log.d(TAG, "resendVerificationCode: $phone")

        val options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallBacks!!)
            .setForceResendingToken(token!!).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyingPhoneNumberWithCode(verificationId: String?, code: String) {
        Log.d(TAG, "verifyingPhoneNumberWithCode: $verificationId $code")
        processDialog.setMessage("Verifying Code")
        processDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)


        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: ")
        processDialog.setMessage("Logging In")

        firebaseAuth.signInWithCredential(credential).addOnSuccessListener {
            //로그인 성공
            val phone = firebaseAuth.currentUser?.phoneNumber
            Toast.makeText(this, "Loged In as $phone", Toast.LENGTH_SHORT).show()

            firestore?.collection("users")?.document(FirebaseAuth.getInstance().currentUser?.uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var item = documentSnapshot.toObject(UserDTO::class.java)

                    if (item == null) {
                        startActivity(Intent(this, SelectRegionActivity::class.java))
                        finish()
                        println("null")
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        println("not null")
                    }
                }


        }.addOnFailureListener { e ->
            //로그인 실패
            processDialog.dismiss()
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()

        }


    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}