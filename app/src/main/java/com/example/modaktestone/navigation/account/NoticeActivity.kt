package com.example.modaktestone.navigation.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.databinding.ActivityNoticeBinding
import com.example.modaktestone.databinding.ItemNoticeBinding
import com.example.modaktestone.navigation.administrator.UploadNoticeActivity
import com.example.modaktestone.navigation.model.NoticeDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat

class NoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeBinding

    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var uid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
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

        binding.noticeRecyclerView.adapter = NoticeRecyclerViewAdapter()
        binding.noticeRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.noticeBtnUpload.setOnClickListener { v ->
            var intent = Intent(v.context, UploadNoticeActivity::class.java)
            startActivity(intent)
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

    inner class NoticeRecyclerViewAdapter : RecyclerView.Adapter<NoticeRecyclerViewAdapter.CustomViewHolder>() {
        var noticeDTOs : ArrayList<NoticeDTO> = arrayListOf()

        var noticeUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("notices")?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreExeption ->
                    if (querySnapshot == null) return@addSnapshotListener
                    noticeDTOs.clear()
                    noticeUidList.clear()
                    for (snapshot in querySnapshot.documents) {
                        var item = snapshot.toObject(NoticeDTO::class.java)
                        noticeDTOs.add(item!!)
                        noticeUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }

        inner class CustomViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): NoticeRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: NoticeRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemInquiryAnswerStatus.visibility = View.GONE

            holder.binding.itemNoticeTvTitle.text = noticeDTOs[position].title
            holder.binding.itemNoticeTvTimestamp.text =
                SimpleDateFormat("MM/dd HH:mm").format(noticeDTOs!![position].timestamp)

            holder.binding.itemNoticeBtn.setOnClickListener { v ->
                var intent = Intent(v.context, NoticeDetailActivity::class.java)

                intent.putExtra("destinationTitle", noticeDTOs[position].title)
                intent.putExtra("destinationExplain", noticeDTOs[position].explain)
                intent.putExtra(
                    "destinationTimestamp",
                    SimpleDateFormat("MM/dd HH:mm").format(noticeDTOs[position].timestamp)
                )
                intent.putExtra("contentUid", noticeUidList[position])
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return noticeDTOs.size
        }

    }
}