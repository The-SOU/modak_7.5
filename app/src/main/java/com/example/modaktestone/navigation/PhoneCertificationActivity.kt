package com.example.modaktestone.navigation

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.modaktestone.MainActivity
import com.example.modaktestone.databinding.ActivityPhoneCertificationBinding
import com.example.modaktestone.navigation.model.UserDTO
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
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
                countDown("000500")
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

    fun countDown(time: String) {
        var conversionTime: Long = 0

        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간
        var getHour = time.substring(0, 2)
        var getMin = time.substring(2, 4)
        var getSecond = time.substring(4, 6)

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) === "0") {
            getHour = getHour.substring(1, 2)
        }
        if (getMin.substring(0, 1) === "0") {
            getMin = getMin.substring(1, 2)
        }
        if (getSecond.substring(0, 1) === "0") {
            getSecond = getSecond.substring(1, 2)
        }

        // 변환시간
        conversionTime =
            java.lang.Long.valueOf(getHour) * 1000 * 3600 + java.lang.Long.valueOf(getMin) * 60 * 1000 + java.lang.Long.valueOf(
                getSecond
            ) * 1000

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        object : CountDownTimer(conversionTime, 1000) {
            // 특정 시간마다 뷰 변경
            override fun onTick(millisUntilFinished: Long) {

                // 시간단위
                var hour = (millisUntilFinished / (60 * 60 * 1000)).toString()

                // 분단위
                val getMin = millisUntilFinished - millisUntilFinished / (60 * 60 * 1000)
                var min = (getMin / (60 * 1000)).toString() // 몫

                // 초단위
                var second = (getMin % (60 * 1000) / 1000).toString() // 나머지

                // 밀리세컨드 단위
                val millis = (getMin % (60 * 1000) % 1000).toString() // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length == 1) {
                    hour = "0$hour"
                }

                // 분이 한자리면 0을 붙인다
                if (min.length == 1) {
                    min = "0$min"
                }

                // 초가 한자리면 0을 붙인다
                if (second.length == 1) {
                    second = "0$second"
                }
                binding.phoneTvTimer.setText("${min}분${second}초")
            }

            // 제한시간 종료시
            override fun onFinish() {

                // 변경 후

                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지
            }
        }.start()
    }
}