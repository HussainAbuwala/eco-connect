package com.example.ecoconnect

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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
        if(scannedImg == null){
            scannedImg = BitmapFactory.decodeResource(resources, R.drawable.missing_product_image)
        }

        //binding.ivPhoto.setImageBitmap(scannedImg)

        GlobalScope.launch(Dispatchers.IO) {
            val productInfo = find_product_info(barcode)
            withContext(Dispatchers.Main){
                binding.tvScanResult.text = "Barcode: $barcode"
                displayProductInfo(productInfo)
            }
        }

    }

    fun find_product_info(barcode: String?): Product? {
        val client = OkHttpClient()
        val url = "https://world.openfoodfacts.org/api/v2/product/$barcode.json?fields=packagings,ecoscore_grade,nutriscore_grade,nova_group"
        val request = Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .build()
        val response = client.newCall(request).execute()
        val json = response.body().string()
        val gson = GsonBuilder().create()
        val result = gson.fromJson(json, Product::class.java)

        return result
    }

    fun displayProductInfo(productInfo: Product?){

        productInfo?.status?.let {
            if(it == 0){
                val img = BitmapFactory.decodeResource(resources, R.drawable.food_not_found)
                binding.ivPhoto.setImageBitmap(img)
            }
        }

        productInfo?.product?.nutriscore_grade?.let {
            Log.d("find_product_info","nutriscore_grade: $it")
            setScores("ic_nutriscore_$it",binding.ivNutriScore)
        }

        productInfo?.product?.nova_group?.let {
            Log.d("find_product_info","nova_group: $it")
            setScores("ic_nova_group_$it",binding.ivNovaScore)
        }

        productInfo?.product?.packagings?.let {
            it.forEach{
                Log.d("find_product_info","Material: ${it.material}, Recycling: ${it.recycling}, Shape: ${it.shape}")
            }
        }
    }

    private fun processEcoScore(productInfo: Product?){
        val x = productInfo?.product?.ecoscore_grade?.let {
            if(it == "not-applicable"){
                setScores("ic_ecoscore_unknown",binding.ivEcoScore)
            }
            setScores("ic_ecoscore_$it", binding.ivEcoScore)
        }

    }

    private fun setScores(scoreString: String, imgView: ImageView, ){
        val context: Context = imgView.getContext()
        val id: Int = context.getResources().getIdentifier(scoreString, "drawable", context.getPackageName())
        imgView.setImageResource(id)
    }


}