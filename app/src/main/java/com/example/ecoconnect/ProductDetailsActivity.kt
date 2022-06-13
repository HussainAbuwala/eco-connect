package com.example.ecoconnect

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ecoconnect.databinding.ActivityMainBinding
import com.example.ecoconnect.databinding.ActivityProductDetailsBinding
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

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
        GlobalScope.launch(Dispatchers.IO) {
            // ...
            find_product_info("3017620422003")
        }

    }

    suspend fun find_product_info(barcode: String?) {

        val client = OkHttpClient()
        val url = "https://world.openfoodfacts.org/api/v2/product/$barcode.json?fields=packagings,ecoscore_grade"
        val request = Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .build()
        val response = client.newCall(request).execute()
        val json = response.body().string()
        val gson = GsonBuilder().create()
        val result = gson.fromJson(json, Product::class.java)

        result?.product?.ecoscore_grade?.let {
            Log.d("find_product_info","Ecoscore_grade: $it")
            withContext(Dispatchers.Main){
                Toast.makeText(this@ProductDetailsActivity, "Ecoscore_grade: $it",Toast.LENGTH_LONG).show()
            }
        }

        result?.product?.packagings?.let {
            it.forEach{
                Log.d("find_product_info","Material: ${it.material}, Recycling: ${it.recycling}, Shape: ${it.shape}")
            }
        }
    }

}