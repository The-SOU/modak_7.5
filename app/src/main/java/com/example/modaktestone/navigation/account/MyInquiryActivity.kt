package com.example.modaktestone.navigation.account

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ActivityMyInquiryBinding
import com.example.modaktestone.databinding.ItemNoticeBinding
import com.example.modaktestone.navigation.model.InquiryDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat

class MyInquiryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyInquiryBinding

    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var uid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyInquiryBinding.inflate(layoutInflater)
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

        //업로드 버튼 클릭
        binding.inquiryBtnUpload.setOnClickListener { v ->
            var intent = Intent(v.context, AddInquiryActivity::class.java)
            startActivity(intent)
        }

        //리사이클러뷰 어댑터
        binding.inquiryRecyclerView.adapter = MyInquiryRecyclerViewAdapter()
        binding.inquiryRecyclerView.layoutManager = LinearLayoutManager(this)
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

    inner class MyInquiryRecyclerViewAdapter :
        RecyclerView.Adapter<MyInquiryRecyclerViewAdapter.CustomViewHolder>() {
        var inquiryDTOs: ArrayList<InquiryDTO> = arrayListOf()

        var inquiryUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("inquiries")?.whereEqualTo("inquire", uid)
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreExeption ->
                    if(querySnapshot==null)return@addSnapshotListener
                    inquiryDTOs.clear()
                    inquiryUidList.clear()
                    for(snapshot in querySnapshot.documents){
                        var item = snapshot.toObject(InquiryDTO::class.java)
                        inquiryDTOs.add(item!!)
                        inquiryUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }

        inner class CustomViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyInquiryRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        @SuppressLint("ResourceAsColor")
        override fun onBindViewHolder(
            holder: MyInquiryRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            if(inquiryDTOs[position].answerCount==1){
                holder.binding.itemInquiryAnswerStatus.text = "<답변 완료>"
                holder.binding.itemInquiryAnswerStatus.setTextColor(R.color.newcontent)
            }

            holder.binding.itemNoticeTvTitle.text = inquiryDTOs[position].title

            holder.binding.itemNoticeTvTimestamp.text = SimpleDateFormat("MM/dd HH:mm").format(inquiryDTOs!![position].timestamp)

            holder.binding.itemNoticeBtn.setOnClickListener { v ->
                var intent = Intent(v.context, NoticeDetailActivity::class.java)


            }
        }

        override fun getItemCount(): Int {
            return inquiryDTOs.size
        }

    }
}