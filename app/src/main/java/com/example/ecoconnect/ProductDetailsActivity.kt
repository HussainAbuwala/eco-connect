package com.example.ecoconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ecoconnect.databinding.ActivityMainBinding
import com.example.ecoconnect.databinding.ActivityProductDetailsBinding

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }
}