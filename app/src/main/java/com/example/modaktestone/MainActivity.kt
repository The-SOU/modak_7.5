package com.example.modaktestone

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.example.modaktestone.databinding.ActivityMainBinding
import com.example.modaktestone.navigation.AccountFragment
import com.example.modaktestone.navigation.AlarmFragment
import com.example.modaktestone.navigation.BoardFragment
import com.example.modaktestone.navigation.DetailViewFragment
import com.example.modaktestone.navigation.util.FcmPush
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)

        binding.bottomNavigation.selectedItemId = R.id.action_home

        binding.bottomNavigation.itemIconTintList = null

        registerPushToken()



    }

    override fun onStop() {
        super.onStop()
        println("good")
        FcmPush.instance.sendMessage("vAIQa8qyN1XJxCsVjPiSPv1gbXi1", "hi", "bye")
    }

    fun registerPushToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val token = task.result
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String, Any>()
            map["pushToken"] = token!!

            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
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