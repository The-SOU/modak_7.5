package com.example.modaktestone.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.databinding.ActivityBoardcontentBinding
import com.example.modaktestone.databinding.ActivityWelfareNoticeBinding
import com.example.modaktestone.databinding.ItemContentBinding
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class WelfareNoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelfareNoticeBinding

    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var region: String? = null

    var destinationCategory: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelfareNoticeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        destinationCategory = binding.welfareNoticeTvBoardname.text.toString()

        //툴바 설정
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

        //글쓰기 버튼 클릭할 때
        binding.welfareNoticeBtnUpload.setOnClickListener { v ->
            var intent = Intent(v.context, AddContentActivity::class.java)
            intent.putExtra("selectedCategory", destinationCategory)
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

    inner class WelfareNoticeRecyclerViewAdapter :
        RecyclerView.Adapter<WelfareNoticeRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        //컨텐츠들 줄세우기.
        var contentUidList: ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("users")?.document(auth?.currentUser?.uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                    region = userDTO?.region
                    firestore?.collection("contents")?.whereEqualTo("region", region)
                        ?.whereEqualTo("contentCategory", destinationCategory)
                        ?.addSnapshotListener { querySnapshot, firebaseFirestoreExeption ->
                            contentDTOs.clear()
                            contentUidList.clear()
                            if (querySnapshot == null) return@addSnapshotListener

                            for (snapshot in querySnapshot!!.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                                contentUidList.add(snapshot.id)
                            }
                            notifyDataSetChanged()
                        }
                }
        }


        inner class CustomViewHolder(val binding: ItemContentBinding) :
            RecyclerView.ViewHolder(binding.root)


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): WelfareNoticeRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: WelfareNoticeRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.contentTextviewTitle.text = contentDTOs!![position].title

            holder.binding.contentTextviewExplain.text = contentDTOs!![position].explain

            holder.binding.contentTextviewUsername.text = contentDTOs!![position].userName

            holder.binding.contentTextviewTimestamp.text =
                SimpleDateFormat("MM/dd HH:mm").format(contentDTOs!![position].timestamp)

            holder.binding.contentTextviewCommentcount.text =
                contentDTOs!![position].commentCount.toString()

            holder.binding.contentTextviewFavoritecount.text =
                contentDTOs!![position].favoriteCount.toString()

            holder.binding.contentLinearLayout.setOnClickListener { v ->
                var intent = Intent(v.context, DetailContentActivity::class.java)

                intent.putExtra("destinationUsername", contentDTOs[position].userName)

                intent.putExtra("destinationTitle", contentDTOs[position].title)
                intent.putExtra("destinationExplain", contentDTOs[position].explain)
                intent.putExtra(
                    "destinationTimestamp",
                    SimpleDateFormat("MM/dd HH:mm").format(contentDTOs[position].timestamp)
                )
                intent.putExtra(
                    "destinationCommentCount",
                    contentDTOs[position].commentCount.toString()
                )
                intent.putExtra(
                    "destinationFavoriteCount",
                    contentDTOs[position].favoriteCount.toString()
                )
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("contentUid", contentUidList[position])
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

}