package com.example.iptvplayer.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.iptvplayer.R
import com.example.iptvplayer.databinding.ActivitySearchBinding
import com.example.iptvplayer.extensions.lightNavigationBar
import com.example.iptvplayer.extensions.lightStatusBar
import com.example.iptvplayer.extensions.setStatusNaviColor
import com.example.matheasyapp.base.BaseActivity

class SearchActivity : BaseActivity<ActivitySearchBinding>(ActivitySearchBinding::inflate) {


    override fun viewCreated() {
        setStatusNaviColor()
        lightStatusBar()
        lightNavigationBar()

        binding.apply {


        }

        onClick()
        handleTextChange()

    }

    private fun onClick() {
        binding.tvCancle.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.icClear.setOnClickListener {
            binding.edSearch.text.clear()
        }
    }

    private fun handleTextChange() {


        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edSearch.text.isNotEmpty()){
                    binding.icClear.visibility = View.VISIBLE
                }else{
                    binding.icClear.visibility = View.GONE
                }
             }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

}