package com.example.modaktestone.navigation.recyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.databinding.ItemRepeatboardBinding
import com.example.modaktestone.navigation.BoardContentActivity
import com.example.modaktestone.navigation.DetailViewFragment
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RepeatboardXRecyclerViewAdapter :
    RecyclerView.Adapter<RepeatboardXRecyclerViewAdapter.CustomViewHolder>() {
    var firestore: FirebaseFirestore? = FirebaseFirestore.getInstance()
    var currentUserUid: String? = FirebaseAuth.getInstance().currentUser?.uid

    var boardDTO: List<String> =
        listOf("자유게시판", "비밀게시판", "정보게시판", "건강게시판", "트로트게시판", "재취업게시판", "정치게시판")

    inner class CustomViewHolder(val binding: ItemRepeatboardBinding) :
        RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepeatboardXRecyclerViewAdapter.CustomViewHolder {
        val binding =
            ItemRepeatboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RepeatboardXRecyclerViewAdapter.CustomViewHolder,
        position: Int
    ) {
        holder.binding.itemRepeatboardTvBoardname.text = boardDTO[position]

        when (boardDTO[position]) {
            "자유게시판" -> {
                var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                firestore?.collection("users")?.document(currentUserUid!!)
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (documentSnapshot == null) return@addSnapshotListener
                        var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                        firestore?.collection("contents")
                            ?.whereEqualTo("region", regionDTO!!.region)
                            ?.whereEqualTo("contentCategory", "자유게시판")
                            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                contentDTOs.clear()
                                if (documentSnapshot == null) return@addSnapshotListener
                                for (snapshot in documentSnapshot.documents) {
                                    var item = snapshot.toObject(ContentDTO::class.java)
                                    contentDTOs.add(item!!)
                                }
                                if (contentDTOs.size != 0) {
                                    holder.binding.itemRepeatboardTvBoardcontent.text =
                                        contentDTOs[0].explain.toString()

                                } else {
                                    println(contentDTOs.size)
                                }

                            }

                    }

            }
            "비밀게시판" -> {
                var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                firestore?.collection("users")?.document(currentUserUid!!)
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (documentSnapshot == null) return@addSnapshotListener
                        var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                        firestore?.collection("contents")
                            ?.whereEqualTo("region", regionDTO!!.region)
                            ?.whereEqualTo("contentCategory", "비밀게시판")
                            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                contentDTOs.clear()
                                if (documentSnapshot == null) return@addSnapshotListener
                                for (snapshot in documentSnapshot.documents) {
                                    var item = snapshot.toObject(ContentDTO::class.java)
                                    contentDTOs.add(item!!)
                                }
                                if (contentDTOs.size != 0) {
                                    holder.binding.itemRepeatboardTvBoardcontent.text =
                                        contentDTOs[0].explain.toString()

                                } else {
                                    println(contentDTOs.size)
                                }

                            }

                    }
            }
            "정보게시판" -> {
                var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                firestore?.collection("users")?.document(currentUserUid!!)
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (documentSnapshot == null) return@addSnapshotListener
                        var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                        firestore?.collection("contents")
                            ?.whereEqualTo("region", regionDTO!!.region)
                            ?.whereEqualTo("contentCategory", "정보게시판")
                            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                contentDTOs.clear()
                                if (documentSnapshot == null) return@addSnapshotListener
                                for (snapshot in documentSnapshot.documents) {
                                    var item = snapshot.toObject(ContentDTO::class.java)
                                    contentDTOs.add(item!!)
                                }
                                if (contentDTOs.size != 0) {
                                    holder.binding.itemRepeatboardTvBoardcontent.text =
                                        contentDTOs[0].explain.toString()

                                } else {
                                    println(contentDTOs.size)
                                }

                            }

                    }

            }
            "건강게시판" -> {
                var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                firestore?.collection("users")?.document(currentUserUid!!)
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (documentSnapshot == null) return@addSnapshotListener
                        var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                        firestore?.collection("contents")
                            ?.whereEqualTo("region", regionDTO!!.region)
                            ?.whereEqualTo("contentCategory", "건강게시판")
                            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                contentDTOs.clear()
                                if (documentSnapshot == null) return@addSnapshotListener
                                for (snapshot in documentSnapshot.documents) {
                                    var item = snapshot.toObject(ContentDTO::class.java)
                                    contentDTOs.add(item!!)
                                }
                                if (contentDTOs.size != 0) {
                                    holder.binding.itemRepeatboardTvBoardcontent.text =
                                        contentDTOs[0].explain.toString()

                                } else {
                                    println(contentDTOs.size)
                                }

                            }

                    }
            }
            "트로트게시판" -> {
                var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                firestore?.collection("users")?.document(currentUserUid!!)
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (documentSnapshot == null) return@addSnapshotListener
                        var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                        firestore?.collection("contents")
                            ?.whereEqualTo("region", regionDTO!!.region)
                            ?.whereEqualTo("contentCategory", "트로트게시판")
                            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                contentDTOs.clear()
                                if (documentSnapshot == null) return@addSnapshotListener
                                for (snapshot in documentSnapshot.documents) {
                                    var item = snapshot.toObject(ContentDTO::class.java)
                                    contentDTOs.add(item!!)
                                }
                                if (contentDTOs.size != 0) {
                                    holder.binding.itemRepeatboardTvBoardcontent.text =
                                        contentDTOs[0].explain.toString()

                                } else {
                                    println(contentDTOs.size)
                                }

                            }

                    }
            }
            "재취업게시판" -> {
                var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                firestore?.collection("users")?.document(currentUserUid!!)
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (documentSnapshot == null) return@addSnapshotListener
                        var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                        firestore?.collection("contents")
                            ?.whereEqualTo("region", regionDTO!!.region)
                            ?.whereEqualTo("contentCategory", "재취업게시판")
                            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                contentDTOs.clear()
                                if (documentSnapshot == null) return@addSnapshotListener
                                for (snapshot in documentSnapshot.documents) {
                                    var item = snapshot.toObject(ContentDTO::class.java)
                                    contentDTOs.add(item!!)
                                }
                                if (contentDTOs.size != 0) {
                                    holder.binding.itemRepeatboardTvBoardcontent.text =
                                        contentDTOs[0].explain.toString()

                                } else {
                                    println(contentDTOs.size)
                                }
                            }
                    }
            }
            "정치게시판" -> {
                var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                firestore?.collection("users")?.document(currentUserUid!!)
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (documentSnapshot == null) return@addSnapshotListener
                        var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                        firestore?.collection("contents")
                            ?.whereEqualTo("region", regionDTO!!.region)
                            ?.whereEqualTo("contentCategory", "정치게시판")
                            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                contentDTOs.clear()
                                if (documentSnapshot == null) return@addSnapshotListener
                                for (snapshot in documentSnapshot.documents) {
                                    var item = snapshot.toObject(ContentDTO::class.java)
                                    contentDTOs.add(item!!)
                                }
                                if (contentDTOs.size != 0) {
                                    holder.binding.itemRepeatboardTvBoardcontent.text =
                                        contentDTOs[0].explain.toString()

                                } else {
                                    println(contentDTOs.size)
                                }

                            }

                    }
            }
        }

        holder.binding.itemRepeatboardTvBoardname.setOnClickListener { v ->
            var intent = Intent(v.context, BoardContentActivity::class.java)
            var activity = DetailViewFragment()
            intent.putExtra("destinationCategory", boardDTO[position])
        }

    }

    override fun getItemCount(): Int {
        return boardDTO.size
    }


}