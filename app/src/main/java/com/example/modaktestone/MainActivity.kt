package com.example.modaktestone

import android.app.Notification
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.modaktestone.databinding.ActivityMainBinding
import com.example.modaktestone.navigation.*
import com.example.modaktestone.navigation.model.PushDTO
import com.example.modaktestone.navigation.util.FcmPush
import com.google.android.gms.common.api.Response
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import fcm.androidtoandroid.FirebasePush
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "Enter your Key"
    private val contentType = "application/json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)

        binding.bottomNavigation.selectedItemId = R.id.action_home

        binding.bottomNavigation.itemIconTintList = null



        retrieveAndStoreToken()


    }

    private fun retrieveAndStoreToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful){
                val token: String? = task.result

                val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

                FirebaseDatabase.getInstance().getReference("tokens").child(userId!!).setValue(token)
            }
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                var detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, detailViewFragment).commit()
                return true
            }
            R.id.action_board -> {
                var boardFragment = BoardFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, boardFragment)
                    .commit()
                return true
            }
            R.id.action_alarm -> {
                var alarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, alarmFragment)
                    .commit()
                return true
            }
            R.id.action_account -> {
                var accountFragment = AccountFragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                bundle.putString("destinationUid", uid)
                accountFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, accountFragment).commit()
                return true
            }
        }
        return false
    }
}