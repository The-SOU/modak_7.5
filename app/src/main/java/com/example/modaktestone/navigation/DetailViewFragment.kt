package com.example.modaktestone.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.modaktestone.R
import com.example.modaktestone.databinding.FragmentDetailBinding
import com.example.modaktestone.databinding.ItemBestcontentBinding
import com.example.modaktestone.databinding.ItemPagerBinding
import com.example.modaktestone.databinding.ItemRepeatboardBinding
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.viewPager.ImageSlideFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_image_slide.view.*
import kotlinx.android.synthetic.main.item_repeatboard.*
import org.koin.android.ext.android.bind

class DetailViewFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    var firestore: FirebaseFirestore? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        //초기화
        firestore = FirebaseFirestore.getInstance()

        //자주찾는 게시판 어뎁터와 매니저
        binding.detailviewRecyclerviewRepeatboard.adapter = RepeatboardRecyclerViewAdapter()
        binding.detailviewRecyclerviewRepeatboard.layoutManager = LinearLayoutManager(this.context)

        //베스트게시글 어뎁터와 매니저
        binding.detailviewRecyclerviewBestcontent.adapter = BestContentRecyclerViewAdapter()
        binding.detailviewRecyclerviewBestcontent.layoutManager = LinearLayoutManager(this.context)

        //지역 내 정보 어댑터와 매니저
        binding.detailviewRecyclerviewSociety.adapter = DetailSocietyRecyclerViewAdapter()
        binding.detailviewRecyclerviewSociety.layoutManager = LinearLayoutManager(this.context)

        //동호회 홍보 어댑터와 매니저
        binding.detailviewRecyclerviewClub.adapter = DetailClubRecyclerViewAdapter()
        binding.detailviewRecyclerviewClub.layoutManager = LinearLayoutManager(this.context)

        binding.detailviewBtnRepeatboard.setOnClickListener {
            var fragment = BoardFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_content, fragment)?.commit()
        }

        //추천 정보 관련 내용들
        var requestItem: ArrayList<Int> = arrayListOf(
            R.drawable.information_first,
            R.drawable.information_second,
            R.drawable.information_third
        )
        var titleItem: ArrayList<String> = arrayListOf(
            "네이버 인턴의 첫 장보기 스토리",
            "미래에셋 증권 이벤트",
            "영화 <레 미제라블> 시사회 이벤트"
        )
        var contentItem: ArrayList<String> = arrayListOf(
            "네이버 장보기로 최대 10% 적립 받고,\n멤버십 1개월 무료 이용하세요!",
            "주식의 시작은 미래에셋에서\n케잌과 커피 한 잔 받아가세요!",
            "<레 미제라블> 예고편 감상 후 기대평을 써주세요!\n추첨을 통해 시사회에 초대합니다!"
        )

        //추천정보 뷰페이저 어댑터
        binding.viewPager.adapter = ViewPagerAdapter(requestItem, titleItem, contentItem)
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        //
        binding.dotsIndicator.setViewPager2(binding.viewPager)







        return view
//        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
//        return super.onCreateView(inflater, container, savedInstanceState)
    }


    inner class RepeatboardRecyclerViewAdapter :
        RecyclerView.Adapter<RepeatboardRecyclerViewAdapter.CustomViewHolder>() {
        var boardDTO: List<String> =
            listOf("자유게시판", "비밀게시판", "정보게시판", "건강게시판", "트로트게시판", "재취업게시판", "정치게시판")

        inner class CustomViewHolder(val binding: ItemRepeatboardBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RepeatboardRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemRepeatboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: RepeatboardRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemRepeatboardTvBoardname.text = boardDTO[position]

            when (boardDTO[position]) {
                "자유게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
                    firestore?.collection("contents")?.whereEqualTo("contentCategory", "자유게시판")
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            holder.binding.itemRepeatboardTvBoardcontent.text =
                                contentDTOs[0].explain.toString()
                        }
                }
                "비밀게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
                    firestore?.collection("contents")?.whereEqualTo("contentCategory", "비밀게시판")
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            holder.binding.itemRepeatboardTvBoardcontent.text =
                                contentDTOs[0].explain.toString()

                        }
                }
                "정보게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
                    firestore?.collection("contents")?.whereEqualTo("contentCategory", "정보게시판")
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            holder.binding.itemRepeatboardTvBoardcontent.text =
                                contentDTOs[0].explain.toString()

                        }

                }
                "건강게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
                    firestore?.collection("contents")?.whereEqualTo("contentCategory", "건강게시판")
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            holder.binding.itemRepeatboardTvBoardcontent.text =
                                contentDTOs[0].explain.toString()

                        }


                }
                "트로트게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
                    firestore?.collection("contents")?.whereEqualTo("contentCategory", "트로트게시판")
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            holder.binding.itemRepeatboardTvBoardcontent.text =
                                contentDTOs[0].explain.toString()

                        }


                }
                "재취업게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
                    firestore?.collection("contents")?.whereEqualTo("contentCategory", "재취업게시판")
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            holder.binding.itemRepeatboardTvBoardcontent.text =
                                contentDTOs[0].explain.toString()

                        }


                }
                "정치게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
                    firestore?.collection("contents")?.whereEqualTo("contentCategory", "정치게시판")
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            holder.binding.itemRepeatboardTvBoardcontent.text =
                                contentDTOs[0].explain.toString()

                        }


                }
            }

            holder.binding.itemRepeatboardTvBoardname.setOnClickListener { v ->
                var intent = Intent(v.context, BoardContentActivity::class.java)
                intent.putExtra("destinationCategory", boardDTO[position])
                startActivity(intent)
            }

        }

        override fun getItemCount(): Int {
            return boardDTO.size
        }
    }

    fun getBoardContent(boardname: String) {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        firestore?.collection("contents")?.whereEqualTo("contentCategory", boardname)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                if (documentSnapshot == null) return@addSnapshotListener
                for (snapshot in documentSnapshot.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                }
                item_repeatboard_tv_boardcontent.text = contentDTOs[0].explain.toString()

            }

    }

    inner class BestContentRecyclerViewAdapter :
        RecyclerView.Adapter<BestContentRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("contents")?.orderBy("favoriteCount", Query.Direction.DESCENDING)
                ?.limit(2)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    if (documentSnapshot == null) return@addSnapshotListener
                    for (snapshot in documentSnapshot.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BestContentRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: BestContentRecyclerViewAdapter.CustomViewHolder,
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

    inner class DetailSocietyRecyclerViewAdapter :
        RecyclerView.Adapter<DetailSocietyRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("contents")?.whereEqualTo("contentCategory", "지역 내 정보")
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

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DetailSocietyRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: DetailSocietyRecyclerViewAdapter.CustomViewHolder,
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

    inner class DetailClubRecyclerViewAdapter :
        RecyclerView.Adapter<DetailClubRecyclerViewAdapter.CustomViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("contents")?.whereEqualTo("contentCategory", "동호회 홍보")
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

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DetailClubRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: DetailClubRecyclerViewAdapter.CustomViewHolder,
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

    inner class ViewPagerAdapter(
        itemList: ArrayList<Int>,
        titleList: ArrayList<String>,
        contentList: ArrayList<String>
    ) :
        RecyclerView.Adapter<ViewPagerAdapter.CustomViewHolder>() {
        var itemDTO = itemList

        var titleDTO = titleList

        var contentDTO = contentList

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
}