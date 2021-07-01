package com.example.modaktestone.navigation.viewPager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ItemPagerBinding

class ViewPagerAdapter: RecyclerView.Adapter<ViewPagerAdapter.CustomViewHolder>() {
    var itemDTO: ArrayList<Int> = arrayListOf(
        R.drawable.information_first,
        R.drawable.information_second,
        R.drawable.information_third
    )
    var titleDTO: ArrayList<String> = arrayListOf(
        "네이버 인턴의 첫 장보기 스토리",
        "미래에셋 증권 이벤트",
        "영화 <레 미제라블> 시사회 이벤트"
    )
    var contentDTO: ArrayList<String> = arrayListOf(
        "네이버 장보기로 최대 10% 적립 받고,\n멤버십 1개월 무료 이용하세요!",
        "주식의 시작은 미래에셋에서\n케잌과 커피 한 잔 받아가세요!",
        "<레 미제라블> 예고편 감상 후 기대평을 써주세요!\n추첨을 통해 시사회에 초대합니다!"
    )

    inner class CustomViewHolder(val binding: ItemPagerBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerAdapter.CustomViewHolder {
        val binding =
            ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.CustomViewHolder, position: Int) {
        holder.binding.imgPager.setImageResource(itemDTO[position])

        holder.binding.pagerTvTitle.text = titleDTO[position]

        holder.binding.pagerTvContent.text = contentDTO[position]
    }

    override fun getItemCount(): Int {
        return itemDTO.size
    }
}