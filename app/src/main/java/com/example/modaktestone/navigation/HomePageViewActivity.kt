package com.example.modaktestone.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ActivityHomepageViewBinding
import com.example.modaktestone.databinding.ActivityReportViewBinding
import com.example.modaktestone.databinding.ItemReportBinding
import com.example.modaktestone.navigation.model.ReportDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.example.modaktestone.navigation.model.WelfareCenterDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.android.bind
import java.text.SimpleDateFormat

class HomePageViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomepageViewBinding
    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    var destinationRegion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //초기화
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        //인텐트 값 받기
        destinationRegion = intent.getStringExtra("destinationRegion")

        //툴바
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

        //리사이클러뷰 어댑터
        binding.homeRecyclerview.adapter = HomepageRecyclerViewAdapter()
        binding.homeRecyclerview.layoutManager = LinearLayoutManager(this)


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

    inner class HomepageRecyclerViewAdapter :
        RecyclerView.Adapter<HomepageRecyclerViewAdapter.CustomViewHolder>() {
        var welfareCenterDTOs : ArrayList<WelfareCenterDTO> = arrayListOf()


        init {

            firestore?.collection("welfareCenters")?.whereEqualTo("region", destinationRegion)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    welfareCenterDTOs.clear()
                    if(querySnapshot == null)return@addSnapshotListener

                    for(snapshot in querySnapshot!!.documents){
                        var item = snapshot.toObject(WelfareCenterDTO::class.java)
                        welfareCenterDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }

        }

        inner class CustomViewHolder(val binding: ItemReportBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): HomepageRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: HomepageRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {

            holder.binding.itemReportContent.text = welfareCenterDTOs[position].name

            holder.binding.itemReportContent.setOnClickListener {
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(welfareCenterDTOs[position].url) )
                startActivity(intent)
            }


        }

        override fun getItemCount(): Int {
            return welfareCenterDTOs.size
        }

    }


}