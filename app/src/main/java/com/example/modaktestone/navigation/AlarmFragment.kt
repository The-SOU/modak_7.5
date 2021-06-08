package com.example.modaktestone.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.R
import com.example.modaktestone.databinding.FragmentAlarmBinding
import com.example.modaktestone.navigation.model.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_alarm.view.*
import kotlinx.android.synthetic.main.item_comment.view.*

class AlarmFragment : Fragment() {
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.alarmfragmentRecyclerview.adapter = AlarmRecyclerViewAdapter()
        binding.alarmfragmentRecyclerview.layoutManager = LinearLayoutManager(this.activity)

        //툴바 설정
        val toolbar = binding.myToolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        val ab = (activity as AppCompatActivity?)!!.supportActionBar
        ab!!.setDisplayShowTitleEnabled(false)
        ab!!.setDisplayShowCustomEnabled(true)


        return view
    }

    inner class AlarmRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    alarmDTOList.clear()
                    if(querySnapshot == null)return@addSnapshotListener

                    for(snapshot in querySnapshot.documents) {
                        alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            //아이템 코멘트를 그대로 쓸 것이다
            val view = LayoutInflater.from(activity).inflate(R.layout.item_alarm, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView

            when(alarmDTOList[position].kind){
                //공감 알람일 때
                0 -> {
                    val str_0 = alarmDTOList[position].userName + getString(R.string.alarm_favorite)
                    view.alarm_tv_content.text = str_0
                }
                //댓글 알람일 때
                1 -> {
                    val str_1 = alarmDTOList[position].userName + getString(R.string.alarm_comment)
                    view.alarm_tv_content.text = str_1
                }
            }
        }

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

    }
}