package com.example.modaktestone.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.databinding.ActivityHomeRegionBinding
import com.example.modaktestone.databinding.ActivityHomepageViewBinding
import com.example.modaktestone.databinding.ItemReportBinding

class HomeRegionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeRegionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeRegionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //리사이클러뷰 어댑터 설정
        binding.homeregionRecyclerview.adapter = HomeRegionRecyclerViewAdapter()
        binding.homeregionRecyclerview.layoutManager = LinearLayoutManager(this)

        //툴바 설정
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

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

    inner class HomeRegionRecyclerViewAdapter : RecyclerView.Adapter<HomeRegionRecyclerViewAdapter.CustomViewHolder>() {
        var regionDTO: List<String> =
            listOf("서울", "부산", "대구", "인천", "광주", "대전", "울산", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주")

        inner class CustomViewHolder(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): HomeRegionRecyclerViewAdapter.CustomViewHolder {
            val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: HomeRegionRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemReportContent.text = regionDTO[position]

            holder.binding.itemReportContent.setOnClickListener { v ->
                var intent = Intent(v.context, HomePageViewActivity::class.java)
                intent.putExtra("destinationRegion", regionDTO[position])
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return regionDTO.size
        }

    }
}