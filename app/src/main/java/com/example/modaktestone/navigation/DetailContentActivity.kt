package com.example.modaktestone.navigation

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ActivityDetailContentBinding
import com.example.modaktestone.databinding.ItemCommentBinding
import com.example.modaktestone.navigation.model.AlarmDTO
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.example.modaktestone.navigation.util.FcmPush
import com.example.modaktestone.navigation.util.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat


class DetailContentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailContentBinding

    var destinationUserName: String? = null
    var destinationTitle: String? = null
    var destinationExplain: String? = null
    var destinationTimestamp: String? = null
    var destinationCommentCount: Int? = 0
    var destinationFavoriteCount: Int? = 0
    var destinationUid: String? = null
    var contentUid: String? = null

    var editTitle: String? = null
    var editExplain: String? = null


    var uid: String? = null

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    var anonymityDTO = ContentDTO.Comment()

    private lateinit var myDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_detail_content)
        binding = ActivityDetailContentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //변수 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        destinationTitle = intent.getStringExtra("destinationTitle")
        destinationExplain = intent.getStringExtra("destinationExplain")


        //컨텐츠 내용 띄우기
        binding.detailcontentTextviewUsername.text = intent.getStringExtra("destinationUsername")
        binding.detailcontentTextviewTitle.text = intent.getStringExtra("destinationTitle")
        binding.detailcontentTextviewExplain.text = intent.getStringExtra("destinationExplain")
        binding.detailcontentTextviewTimestamp.text = intent.getStringExtra("destinationTimestamp")
        binding.detailcontentTvCommentcount.text = intent.getStringExtra("destinationCommentCount")
        binding.detailcontentTvFavoritecount.text =
            intent.getStringExtra("destinationFavoriteCount")
        destinationUid = intent.getStringExtra("destinationUid")
        contentUid = intent.getStringExtra("contentUid")

        println(contentUid.toString())


        //댓글업로드 클릭 되었을 때
        binding.detailcontentBtnCommentupload.setOnClickListener {
            println("2")
            commentUpload(anonymityDTO)
            requestCommentCount(contentUid!!)
            getCommentCount(contentUid!!)
            commentAlarm(destinationUid!!, binding.detailcontentEdittextComment.text.toString())
            sendNotificationComment(destinationUid!!)
        }

        //좋아요 버튼 클릭되었을 때
        binding.detailcontentLinearFavoritebtn.setOnClickListener {
            favoriteEvent(contentUid!!)
            getFavorite(contentUid!!)
            sendNotificationFavorite(destinationUid!!)
        }

        //댓글창 익명버튼 클릭했을 시
        binding.detailcontentBtnAnonymity.setOnClickListener {
            if (anonymityDTO.anonymity.containsKey(auth?.currentUser?.uid)) {
                anonymityDTO.anonymity.remove(auth?.currentUser?.uid)
                binding.detailcontentImageviewAnonymitybtn.setImageResource(R.drawable.ic_unanonymity)
                println("anonymity delete complete")
            } else {
                anonymityDTO.anonymity[auth?.currentUser?.uid!!] = true
                binding.detailcontentImageviewAnonymitybtn.setImageResource(R.drawable.ic_anonymity)
                println("anonymity add complete")
            }
        }

        //findViewById(R.id.my_toolbar)
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

        binding.detailcontentRecyclerview.adapter = DetailContentRecycleViewAdapter()
        binding.detailcontentRecyclerview.layoutManager = LinearLayoutManager(this)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        if (uid == destinationUid) {
            //이 글의 게시자가 자신의 글을 보고있다면
            inflater.inflate(R.menu.detailcontent_option_menu, menu)
        } else {
            //다른 사람이 게시글을 보고 있다면
            inflater.inflate(R.menu.detailcontent_option_menu_second, menu)
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_edit -> {
                Toast.makeText(this, "글을 수정하겠습니다.", Toast.LENGTH_SHORT).show()
                var editIntent = Intent(this, EditContentActivity::class.java)
                editIntent.putExtra("editTitle", destinationTitle)
                editIntent.putExtra("editExplain", destinationExplain)
                editIntent.putExtra("contentUid", contentUid)
                startActivity(editIntent)
                finish()
                true
            }
            R.id.item_delete -> {
                firestore?.collection("contents")?.document(contentUid!!)?.delete()
                    ?.addOnCompleteListener {
                        Toast.makeText(this, "글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                true
            }
            R.id.item_report -> {
                Toast.makeText(this, "해당 게시자를 신고하였습니다.", Toast.LENGTH_SHORT).show()
                var reportIntent = Intent(this, ReportViewActivity::class.java)
                reportIntent.putExtra("targetContent", contentUid)
                reportIntent.putExtra("targetTitle", destinationTitle )
                reportIntent.putExtra("targetExplain", destinationExplain)
                startActivity(reportIntent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    inner class DetailContentRecycleViewAdapter :
        RecyclerView.Adapter<DetailContentRecycleViewAdapter.CustomViewHolder>() {
        var comments: ArrayList<ContentDTO.Comment> = arrayListOf()


        init {
            FirebaseFirestore.getInstance().collection("contents").document(contentUid!!)
                .collection("comments").orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents!!) {
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }


        inner class CustomViewHolder(val binding: ItemCommentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DetailContentRecycleViewAdapter.CustomViewHolder {
            val binding =
                ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: DetailContentRecycleViewAdapter.CustomViewHolder,
            position: Int
        ) {
            //이름 추가.
            if (comments[position].anonymity.containsKey(comments[position].uid)) {
                holder.binding.commentitemTextviewUsername.text = comments[position].anonymityName
                println("3")
            } else {
                holder.binding.commentitemTextviewUsername.text = comments[position].userName
            }

            holder.binding.commentitemTextviewComment.text = comments[position].comment
            holder.binding.commentitemTextviewTimestamp.text =
                SimpleDateFormat("MM/dd HH:mm").format(comments[position].timestamp)

        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }

    fun commentUpload(anonymity: ContentDTO.Comment) {
        var uid = auth?.currentUser?.uid
        var username: String? = null
        var region: String? = null

        //익명 체크가 되었다
        if (anonymity.anonymity.containsKey(auth?.currentUser?.uid)) {
            var tsDoc = firestore?.collection("contents")?.document(contentUid!!)
            firestore?.runTransaction { transaction ->
                println("7")
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                //익명으로 처음 댓글을 쓰는 거라면
                if (!contentDTO!!.anonymityCommentList.containsKey(auth?.currentUser?.uid!!)) {
                    //리스트에 추가하고
                    contentDTO!!.anonymityCommentList[auth?.currentUser?.uid!!] = true
                    //카운트 +1 하기.
                    contentDTO!!.anonymityCount = contentDTO!!.anonymityCount + 1

                    firestore?.collection("users")?.document(uid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                            username = userDTO?.userName
                            region = userDTO?.region

                            var comment = ContentDTO.Comment()

                            //코멘트 어노니미티에 저장하고.
                            comment.anonymity[auth?.currentUser?.uid!!] = true
                            //새로운 이름 부여.
                            comment.anonymityName = "익명" + contentDTO?.anonymityCount.toString()

                            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
                            comment.comment = binding.detailcontentEdittextComment.text.toString()
                            comment.timestamp = System.currentTimeMillis()
                            comment.userName = username

                            FirebaseFirestore.getInstance().collection("contents")
                                .document(contentUid!!)
                                .collection("comments").document().set(comment)

                            binding.detailcontentEdittextComment.setText("")
                        }

                } else {
                    //익명으로 처음 글을 쓰는 것이 아니라면.
                    firestore?.collection("users")?.document(uid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            println("5")
                            var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                            username = userDTO?.userName
                            region = userDTO?.region

                            var comment = ContentDTO.Comment()
                            //코멘트 어노니미티에 저장하고.
                            comment.anonymity[auth?.currentUser?.uid!!] = true
                            //새로운 이름 부여.
                            comment.anonymityName = "익명" + contentDTO?.anonymityCount.toString()

                            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
                            comment.comment = binding.detailcontentEdittextComment.text.toString()
                            comment.timestamp = System.currentTimeMillis()
                            comment.userName = username

                            FirebaseFirestore.getInstance().collection("contents")
                                .document(contentUid!!)
                                .collection("comments").document().set(comment)

                            binding.detailcontentEdittextComment.setText("")
                        }
                }
                transaction.set(tsDoc, contentDTO)
                return@runTransaction
            }
        } else {
            //익명체크가 안되어 있다면.
            firestore?.collection("users")?.document(uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    println("5")
                    var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                    username = userDTO?.userName
                    region = userDTO?.region

                    var comment = ContentDTO.Comment()

                    comment.uid = FirebaseAuth.getInstance().currentUser?.uid
                    comment.comment = binding.detailcontentEdittextComment.text.toString()
                    comment.timestamp = System.currentTimeMillis()
                    comment.userName = username

                    FirebaseFirestore.getInstance().collection("contents")
                        .document(contentUid!!)
                        .collection("comments").document().set(comment)

                    binding.detailcontentEdittextComment.setText("")
                }
        }


    }

    fun getFavorite(contentUid: String) {
        firestore?.collection("contents")?.document(contentUid)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var contentDTO = documentSnapshot.toObject(ContentDTO::class.java)
                binding.detailcontentTvFavoritecount.text = contentDTO?.favoriteCount.toString()
            }
    }

    fun favoriteEvent(contentUid: String) {
        var tsDoc = firestore?.collection("contents")?.document(contentUid)
        firestore?.runTransaction { transaction ->
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
            //만약 현재 유저 유아디가 페이버릿을 누른적이 있따면
            if (contentDTO!!.favorites.containsKey(uid)) {
                contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                contentDTO?.favorites.remove(uid)
            } else {
                //현재 유저가 페이버릿 누른적이 없다면
                contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                contentDTO?.favorites[uid!!] = true
                favoriteAlarm(destinationUid!!)
            }
            transaction.set(tsDoc, contentDTO)
            return@runTransaction
        }
    }

    fun requestCommentCount(contentUid: String) {
        var tsDoc = firestore?.collection("contents")?.document(contentUid)
        firestore?.runTransaction { transaction ->
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
            if (contentDTO != null) {
                contentDTO?.commentCount = contentDTO?.commentCount!! + 1
            }
            transaction.set(tsDoc, contentDTO!!)
            return@runTransaction
        }
    }

    fun getCommentCount(contentUid: String) {
        firestore?.collection("contents")?.document(contentUid)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var contentDTO = documentSnapshot.toObject(ContentDTO::class.java)
                binding.detailcontentTvCommentcount.text = contentDTO?.commentCount.toString()
            }
    }

    fun favoriteAlarm(destinationUid: String) {
        firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                var alarmDTO = AlarmDTO()
                alarmDTO.destinationUid = destinationUid
                alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
                alarmDTO.userName = userDTO?.userName
                alarmDTO.kind = 0
                alarmDTO.timestamp = System.currentTimeMillis()
                FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

                FcmPush.instance.sendMessage(destinationUid, "hi", "good")
            }

    }

    fun commentAlarm(destinationUid: String, message: String) {
        firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                var alarmDTO = AlarmDTO()
                alarmDTO.destinationUid = destinationUid
                alarmDTO.message = message
                alarmDTO.userName = userDTO?.userName
                alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
                alarmDTO.kind = 1
                alarmDTO.timestamp = System.currentTimeMillis()
                FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
            }
    }

    private fun sendNotificationFavorite(receiverid: String) {

        firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                var userName = userDTO?.userName

                var title = "알림이 왔습니다"
                var text = userName + getString(R.string.alarm_favorite)

                val notification = Notification(text, title, receiverid)

                FirebaseDatabase.getInstance().getReference("Notification").push()
                    .setValue(notification)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Message didn't sent!!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

    }

    private fun sendNotificationComment(receiverid: String) {

        firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var userDTO = documentSnapshot.toObject(UserDTO::class.java)
                var userName = userDTO?.userName

                var title = "알림이 왔습니다"
                var text = userName + getString(R.string.alarm_comment)


                val notification = Notification(text, title, receiverid)


                FirebaseDatabase.getInstance().getReference("Notification").push()
                    .setValue(notification)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Message didn't sent!!", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
    }

    fun anonymityCountEvent() {


        var tsDoc = firestore?.collection("contents")?.document(contentUid!!)
        firestore?.runTransaction { transaction ->
            println("7")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
            //익명으로 처음 댓글을 쓰는 거라면
            if (!contentDTO!!.anonymityCommentList.containsKey(auth?.currentUser?.uid!!)) {
                //리스트에 추가하고
                contentDTO!!.anonymityCommentList[auth?.currentUser?.uid!!] = true
                //카운트 +1 하기.
                contentDTO!!.anonymityCount = contentDTO!!.anonymityCount + 1

            }


            transaction.set(tsDoc, contentDTO)
            return@runTransaction
        }


    }
}


