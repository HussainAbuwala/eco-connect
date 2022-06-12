package com.example.ecoconnect

import android.graphics.Bitmap
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

        val barcode = intent.getStringExtra("EXTRA_BARCODE")
        var scannedImg = intent.getParcelableExtra<Bitmap>("EXTRA_PRODUCT_IMG")

        binding.ivPhoto.setImageBitmap(scannedImg)
        binding.tvScanResult.text = "Barcode: $barcode"

    }


}