package com.example.modaktestone.navigation.recyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.databinding.ItemBestcontentBinding
import com.example.modaktestone.navigation.DetailContentActivity
import com.example.modaktestone.navigation.DetailViewFragment
import com.example.modaktestone.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat

class HotContentXRecyclerViewAdapter: RecyclerView.Adapter<HotContentXRecyclerViewAdapter.CustomViewHolder>() {
    var firestore: FirebaseFirestore? = FirebaseFirestore.getInstance()
    var currentUserUid: String? = FirebaseAuth.getInstance().currentUser?.uid

    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()

    init {

        firestore?.collection("contents")?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.limit(10)?.orderBy("commentCount", Query.Direction.DESCENDING)?.limit(2)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if (documentSnapshot == null) return@addSnapshotListener
                for (snapshot in documentSnapshot.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }

    }

    inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HotContentXRecyclerViewAdapter.CustomViewHolder {
        val binding =
            ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HotContentXRecyclerViewAdapter.CustomViewHolder,
        position: Int
    ) {
        holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

        holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

        holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

        holder.binding.itemBestcontentTvCommentcount.text =
            contentDTOs[position].commentCount.toString()

        holder.binding.itemBestcontentTvFavoritecount.text =
            contentDTOs[position].favoriteCount.toString()

        holder.binding.contentLinearLayout.setOnClickListener { v ->
            var intent = Intent(v.context, DetailContentActivity::class.java)
            if (contentDTOs[position].anonymity.containsKey(contentDTOs[position].uid)) {
                intent.putExtra("destinationUsername", "익명")
            } else {
                intent.putExtra("destinationUsername", contentDTOs[position].userName)
            }
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
            intent.putExtra("destinationUid", contentDTOs[position].uid)
            var activity = DetailViewFragment()
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }
}