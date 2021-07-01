package com.example.modaktestone.navigation.viewPager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ItemPagerBinding

class ViewPagerSecondAdapter: RecyclerView.Adapter<ViewPagerSecondAdapter.CustomViewHolder>() {
    var itemDTO: ArrayList<Int> =
        arrayListOf(
            R.drawable.event_first,
            R.drawable.event_second,
            R.drawable.event_third,
            R.drawable.event_forth
        )
    var titleDTO: ArrayList<String> =
        arrayListOf(
            "연극 <오백에 삼십> 무료 초대 이벤트",
            "전시 <유에민쥔, 한 시대를 웃다!> 초대 이벤트",
            "<모네, 빛을 그리다 _ 영혼의 뮤즈> #기대평 이벤트",
            "뮤지컬 <식구를 찾아서> 무료 초대 이벤트"
        )
    var contentDTO: ArrayList<String> = arrayListOf(
        "20팀 초대(1인 2매, 총 40매)",
        "50명(1인 2매, 총 100매)",
        "50명(1인 2매, 총 100매)",
        "30명(1인 2매, 총 60매)"
    )

    inner class CustomViewHolder(val binding: ItemPagerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerSecondAdapter.CustomViewHolder {
        val binding =
            ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerSecondAdapter.CustomViewHolder, position: Int) {
        holder.binding.imgPager.setImageResource(itemDTO[position])

        holder.binding.pagerTvTitle.text = titleDTO[position]

        holder.binding.pagerTvContent.text = contentDTO[position]
    }

    override fun getItemCount(): Int {
        return itemDTO.size
    }
}