package com.example.modaktestone.navigation.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.modaktestone.databinding.ActivityInquiryDetailBinding
import com.example.modaktestone.databinding.ActivityNoticeBinding
import com.example.modaktestone.databinding.ActivityNoticeDetailBinding

class NoticeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //액션바 설정
        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

        binding.noticeDetailTvTitle.text = intent.getStringExtra("destinationTitle")
        binding.noticeDetailTvExplain.text = intent.getStringExtra("destinationExplain")
        binding.noticeDetailTvTimestamp.text = intent.getStringExtra("destinationTimestamp")
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

}