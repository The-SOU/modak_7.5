package com.example.modaktestone.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.modaktestone.MainActivity
import com.example.modaktestone.R
import com.example.modaktestone.databinding.FragmentDetailBinding
import com.example.modaktestone.databinding.ItemBestcontentBinding
import com.example.modaktestone.databinding.ItemPagerBinding
import com.example.modaktestone.databinding.ItemRepeatboardBinding
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.example.modaktestone.navigation.recyclerView.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image_slide.view.*
import kotlinx.android.synthetic.main.item_repeatboard.*
import java.text.SimpleDateFormat


class DetailViewFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var currentUserUid: String? = null
    var region: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//초기화
        firestore = FirebaseFirestore.getInstance()
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        getMyRegion()
//        region = arguments?.getString("userRegion")


    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.boardcontentTextviewMyregion.text = arguments?.getString("userRegion")

        binding.detailviewBtnRepeatboard.setOnClickListener {
            var fragment = BoardFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_content, fragment)?.commit()
//            var activity = MainActivity()
//            bottom_navigation.setOnNavigationItemSelectedListener(activity)
//            val menu: Menu = bottom_navigation.getMenu()
//            activity.onNavigationItemSelected(menu.findItem(R.id.action_board))


        }
        //자주찾는 게시판 어뎁터와 매니저
        binding.detailviewRecyclerviewRepeatboard.adapter = RepeatboardRecyclerViewAdapter()
        binding.detailviewRecyclerviewRepeatboard.layoutManager = LinearLayoutManager(this.context)

        //베스트게시글 어뎁터와 매니저
        binding.detailviewRecyclerviewBestcontent.adapter = BestContentRecyclerViewAdapter()
        binding.detailviewRecyclerviewBestcontent.layoutManager = LinearLayoutManager(this.context)

        //실시간 인기글 게시판
        binding.detailviewRecyclerviewHotcontent.adapter = HotContentRecyclerViewAdapter()
        binding.detailviewRecyclerviewHotcontent.layoutManager = LinearLayoutManager(this.context)

        //지역 내 정보 어댑터와 매니저
        binding.detailviewRecyclerviewSociety.adapter = DetailSocietyRecyclerViewAdapter()
        binding.detailviewRecyclerviewSociety.layoutManager = LinearLayoutManager(this.context)

        //동호회 홍보 어댑터와 매니저
        binding.detailviewRecyclerviewClub.adapter = DetailClubRecyclerViewAdapter()
        binding.detailviewRecyclerviewClub.layoutManager = LinearLayoutManager(this.context)

        //추천정보 뷰페이저 어댑터
        binding.viewPager.adapter = ViewPagerAdapter()
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        //뷰페이저에 동그라미 인디케이터 추가
        binding.dotsIndicator.setViewPager2(binding.viewPager)

        binding.viewPagerSecond.adapter =
            ViewPagerSecondAdapter()
        binding.viewPagerSecond.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicatorSecond.setViewPager2(binding.viewPagerSecond)

        binding.viewPagerThird.adapter = ViewPagerThirdAdapter()
        binding.viewPagerThird.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicatorThird.setViewPager2(binding.viewPagerThird)


        //상위 4개 버튼 모음
        binding.detailviewBtnHomepage.setOnClickListener { v ->
            var intent = Intent(v.context, HomeRegionActivity::class.java)
            startActivity(intent)
        }

        binding.detailviewBtnNotice.setOnClickListener { v ->
            var intent = Intent(v.context, BoardContentActivity::class.java)
            intent.putExtra("destinationCategory", "복지관 공지사항")
            startActivity(intent)
        }

        return view
    }

    fun getMyRegion() {
        firestore?.collection("users")?.document(currentUserUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                binding.boardcontentTextviewMyregion.text = regionDTO?.region
            }
    }


    inner class RepeatboardRecyclerViewAdapter :
        RecyclerView.Adapter<RepeatboardRecyclerViewAdapter.CustomViewHolder>() {

        var boardDTO: List<String> =
            listOf("자유게시판", "건강게시판", "재취업게시판", "트로트게시판")


        init {
            firestore?.collection("users")?.document(currentUserUid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                    region = regionDTO?.region
                }
        }


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
            println("what $region")
            when (boardDTO[position]) {
                "자유게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("contents")
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
                "건강게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("contents")
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
                "트로트게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("contents")
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
                "재취업게시판" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("contents")
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

    inner class HotContentRecyclerViewAdapter :
        RecyclerView.Adapter<HotContentRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("contents")?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.limit(10)?.orderBy("commentCount", Query.Direction.DESCENDING)?.limit(2)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if (documentSnapshot == null) return@addSnapshotListener
                    for (snapshot in documentSnapshot.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): HotContentRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: HotContentRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {

            holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

            holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

            holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

            holder.binding.itemBestcontentTvCommentcount.text =
                contentDTOs[position].commentCount.toString()

            holder.binding.itemBestcontentTvFavoritecount.text =
                contentDTOs[position].favoriteCount.toString()

            holder.binding.contentLinearLayout.setOnClickListener { v ->
//                var intent = Intent(v.context, DetailContentActivity::class.java)
//                if (contentDTOs[position].anonymity.containsKey(contentDTOs[position].uid)) {
//                    intent.putExtra("destinationUsername", "익명")
//                } else {
//                    intent.putExtra("destinationUsername", contentDTOs[position].userName)
//                }
//                intent.putExtra("destinationTitle", contentDTOs[position].title)
//                intent.putExtra("destinationExplain", contentDTOs[position].explain)
//                intent.putExtra(
//                    "destinationTimestamp",
//                    SimpleDateFormat("MM/dd HH:mm").format(contentDTOs[position].timestamp)
//                )
//                intent.putExtra(
//                    "destinationCommentCount",
//                    contentDTOs[position].commentCount.toString()
//                )
//                intent.putExtra(
//                    "destinationFavoriteCount",
//                    contentDTOs[position].favoriteCount.toString()
//                )
//                intent.putExtra("destinationUid", contentDTOs[position].uid)
//                intent.putExtra("destinationUid", contentDTOs[position].uid)
//
//                startActivity(intent)

                var intent = Intent(v.context, BoardContentActivity::class.java)
                intent.putExtra("destinationCategory", contentDTOs[position].contentCategory)
                startActivity(intent)
            }

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }


    inner class BestContentRecyclerViewAdapter :
        RecyclerView.Adapter<BestContentRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("contents")?.orderBy("favoriteCount", Query.Direction.DESCENDING)
                ?.limit(2)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()

                    if (documentSnapshot == null) return@addSnapshotListener
                    for (snapshot in documentSnapshot.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
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
            val safePosition = holder.adapterPosition
            holder.binding.itemBestcontentTvTitle.text = contentDTOs[safePosition].title

            holder.binding.itemBestcontentTvExplain.text = contentDTOs[safePosition].explain

            holder.binding.itemBestcontentTvUsername.text = contentDTOs[safePosition].userName

            holder.binding.itemBestcontentTvCommentcount.text =
                contentDTOs[safePosition].commentCount.toString()

            holder.binding.itemBestcontentTvFavoritecount.text =
                contentDTOs[safePosition].favoriteCount.toString()

            holder.binding.contentLinearLayout.setOnClickListener { v ->
                var intent = Intent(v.context, DetailContentActivity::class.java)
                if (contentDTOs[safePosition].anonymity.containsKey(contentDTOs[safePosition].uid)) {
                    intent.putExtra("destinationUsername", "익명")
                } else {
                    intent.putExtra("destinationUsername", contentDTOs[safePosition].userName)
                }
                intent.putExtra("destinationTitle", contentDTOs[safePosition].title)
                intent.putExtra("destinationExplain", contentDTOs[safePosition].explain)
                intent.putExtra(
                    "destinationTimestamp",
                    SimpleDateFormat("MM/dd HH:mm").format(contentDTOs[safePosition].timestamp)
                )
                intent.putExtra(
                    "destinationCommentCount",
                    contentDTOs[safePosition].commentCount.toString()
                )
                intent.putExtra(
                    "destinationFavoriteCount",
                    contentDTOs[safePosition].favoriteCount.toString()
                )
                intent.putExtra("destinationUid", contentDTOs[safePosition].uid)
                intent.putExtra("contentUid", contentUidList[safePosition])
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    inner class DetailSocietyRecyclerViewAdapter :
        RecyclerView.Adapter<DetailSocietyRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("users")?.document(currentUserUid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                    firestore?.collection("contents")
                        ?.whereEqualTo("contentCategory", "지역 내 정보")
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

            holder.binding.contentLinearLayout.setOnClickListener { v ->
                var intent = Intent(v.context, BoardContentActivity::class.java)
                intent.putExtra("destinationCategory", "지역 내 정보")
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    inner class DetailClubRecyclerViewAdapter :
        RecyclerView.Adapter<DetailClubRecyclerViewAdapter.CustomViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("users")?.document(currentUserUid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                    firestore?.collection("contents")
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

            holder.binding.contentLinearLayout.setOnClickListener { v ->
                var intent = Intent(v.context, BoardContentActivity::class.java)
                intent.putExtra("destinationCategory", "동호회 홍보")
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    inner class ViewPagerAdapter :
        RecyclerView.Adapter<ViewPagerAdapter.CustomViewHolder>() {
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

    inner class ViewPagerSecondAdapter :
        RecyclerView.Adapter<ViewPagerSecondAdapter.CustomViewHolder>() {
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

        override fun onBindViewHolder(
            holder: ViewPagerSecondAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.imgPager.setImageResource(itemDTO[position])

            holder.binding.pagerTvTitle.text = titleDTO[position]

            holder.binding.pagerTvContent.text = contentDTO[position]
        }

        override fun getItemCount(): Int {
            return itemDTO.size
        }

    }

    inner class ViewPagerThirdAdapter :
        RecyclerView.Adapter<ViewPagerThirdAdapter.CustomViewHolder>() {
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

        override fun onBindViewHolder(
            holder: ViewPagerThirdAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.imgPager.setImageResource(itemDTO[position])

            holder.binding.pagerTvTitle.text = titleDTO[position]

            holder.binding.pagerTvContent.visibility = View.GONE
        }

        override fun getItemCount(): Int {
            return itemDTO.size
        }

    }
}