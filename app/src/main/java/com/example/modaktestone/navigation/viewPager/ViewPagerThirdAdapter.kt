package com.example.modaktestone.navigation.viewPager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ItemPagerBinding

class ViewPagerThirdAdapter: RecyclerView.Adapter<ViewPagerThirdAdapter.CustomViewHolder>() {
    //복지혜택 관련 아이템들
    var itemDTO: ArrayList<Int> = arrayListOf(
        R.drawable.welfare_first,
        R.drawable.welfare_second,
        R.drawable.welfare_third
    )
    var titleDTO: ArrayList<String> =
        arrayListOf(
            "서울노인복지센터 - 취업상담지원 제공",
            "서울노인복지센터 - 급식지원",
            "서울대학교 간호대학교 학생들과 함께하는 건강관리교실 다소니"
        )

    inner class CustomViewHolder(val binding: ItemPagerBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerThirdAdapter.CustomViewHolder {
        val binding =
            ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerThirdAdapter.CustomViewHolder, position: Int) {
        holder.binding.imgPager.setImageResource(itemDTO[position])

        holder.binding.pagerTvTitle.text = titleDTO[position]

        holder.binding.pagerTvContent.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return itemDTO.size
    }
}