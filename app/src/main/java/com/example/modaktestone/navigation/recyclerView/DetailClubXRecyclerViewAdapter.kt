package com.example.modaktestone.navigation.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.databinding.ItemBestcontentBinding
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailClubXRecyclerViewAdapter: RecyclerView.Adapter<DetailClubXRecyclerViewAdapter.CustomViewHolder>() {
    var firestore: FirebaseFirestore? = FirebaseFirestore.getInstance()
    var currentUserUid: String? = FirebaseAuth.getInstance().currentUser?.uid

    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

    init {
        firestore?.collection("users")?.document(currentUserUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                firestore?.collection("contents")?.whereEqualTo("region", "테스트")
                    ?.whereEqualTo("contentCategory", "동호회 홍보")
                    ?.orderBy("timestamp")?.limit(3)
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        contentDTOs.clear()
                        if (documentSnapshot == null) return@addSnapshotListener
                        for (snapshot in documentSnapshot.documents) {
                            var item = snapshot.toObject(ContentDTO::class.java)
                            contentDTOs.add(item!!)
                        }
                        notifyDataSetChanged()
                    }
            }
    }

    inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailClubXRecyclerViewAdapter.CustomViewHolder {
        val binding =
            ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DetailClubXRecyclerViewAdapter.CustomViewHolder,
        position: Int
    ) {
        holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

        holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

        holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

        holder.binding.itemBestcontentTvCommentcount.text =
            contentDTOs[position].commentCount.toString()

        holder.binding.itemBestcontentTvFavoritecount.text =
            contentDTOs[position].favoriteCount.toString()
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }
}