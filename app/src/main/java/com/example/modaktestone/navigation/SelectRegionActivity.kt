package com.example.modaktestone.navigation

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.example.modaktestone.MainActivity
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ActivitySelectRegionBinding
import com.example.modaktestone.navigation.model.SpinnerModel
import com.example.modaktestone.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

class SelectRegionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectRegionBinding

    private lateinit var spinnerAdapterRegion: SpinnerAdapter
    private lateinit var spinnerAdapterSex: SpinnerAdapter
    private lateinit var spinnerAdapterBirth: SpinnerAdapter
    private val listOfRegion = ArrayList<SpinnerModel>()
    private val listOfSex = ArrayList<SpinnerModel>()
    private val listOfBirth = ArrayList<SpinnerModel>()
    var uid: String? = null
    var region: String? = null
    var sex: String? = null
    var birth: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        uid = FirebaseAuth.getInstance().currentUser?.uid

        binding.selectregionBtn.setOnClickListener {
            usernameAndRegion()
            moveMainPage()

        }
        setupSpinnerRegion()
        setupSpinnerSex()
        setupSpinnerBirth()
        setupSpinnerHandler()

        //키보드 숨기기
        binding.layout.setOnClickListener {
            hideKeyboard()
        }




    }


    private fun setupSpinnerRegion() {
        var regionDatas = listOf(
            "지역",
            "서울",
            "부산",
            "대구",
            "인천",
            "광주",
            "대전",
            "울산",
            "경기",
            "강원",
            "충북",
            "충남",
            "전북",
            "전남",
            "경북",
            "경남",
            "제주"
        )

        for (i in regionDatas.indices) {
            val item = SpinnerModel(regionDatas[i])
            listOfRegion.add(item)
        }
        spinnerAdapterRegion =
            com.example.modaktestone.navigation.spinner.SpinnerAdapter(
                this,
                R.layout.item_spinner,
                listOfRegion
            )
        binding.selectregionSpinner.adapter = spinnerAdapterRegion
    }

    private fun setupSpinnerSex() {
        val sexDatas = listOf("성별", "남성", "여성")

        for (i in sexDatas.indices) {
            val item = SpinnerModel(sexDatas[i])
            listOfSex.add(item)
        }
        spinnerAdapterSex =
            com.example.modaktestone.navigation.spinner.SpinnerAdapter(
                this,
                R.layout.item_spinner,
                listOfSex
            )
        binding.selectregionSpinnerSex.adapter = spinnerAdapterSex
    }

    private fun setupSpinnerBirth() {
        val birthDatas = resources.getStringArray(R.array.spinner_birth)

        for (i in birthDatas.indices) {
            val item = SpinnerModel(birthDatas[i])
            listOfBirth.add(item)
        }
        spinnerAdapterBirth = com.example.modaktestone.navigation.spinner.SpinnerAdapter(
            this,
            R.layout.item_spinner,
            listOfBirth
        )
        binding.selectregionSpinnerBirth.adapter = spinnerAdapterBirth
    }

    private fun setupSpinnerHandler() {
        binding.selectregionSpinnerSex.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (!binding.selectregionSpinnerSex.getItemAtPosition(position).equals("성별")) {
                        val item =
                            binding.selectregionSpinnerSex.getItemAtPosition(position) as SpinnerModel
                        sex = item.name
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        binding.selectregionSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (!binding.selectregionSpinner.getItemAtPosition(position).equals("지역")) {
                        val item =
                            binding.selectregionSpinner.getItemAtPosition(position) as SpinnerModel
                        region = item.name
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        binding.selectregionSpinnerBirth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(!binding.selectregionSpinnerBirth.getItemAtPosition(position).equals("출생연도")){
                    val item = binding.selectregionSpinnerBirth.getItemAtPosition(position) as SpinnerModel
                    birth = item.name
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    fun usernameAndRegion() {
        var userDTO = UserDTO()
        userDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        userDTO.region = region
        userDTO.sex = sex
        userDTO.birth = birth
        userDTO.userName = binding.selectregionEdittextName.text.toString()
        FirebaseFirestore.getInstance().collection("users").document(uid!!).set(userDTO)
    }

    fun moveMainPage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun hideKeyboard(){
        val view = this.currentFocus
        if(view != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}