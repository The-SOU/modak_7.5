package com.example.modaktestone.navigation.viewPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.R
import com.example.modaktestone.databinding.FragmentAlarmBinding
import com.example.modaktestone.databinding.FragmentImageSlideBinding
import com.example.modaktestone.databinding.FragmentInformationBinding
import com.example.modaktestone.navigation.model.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_image_slide.*
import kotlinx.android.synthetic.main.item_comment.view.*
import org.koin.android.ext.android.bind

class ImageSlideFragment(val image : Int) : Fragment() {
    private var _binding: FragmentImageSlideBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_slide_image.setImageResource(image)
    }


}