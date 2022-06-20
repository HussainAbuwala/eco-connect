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
    val ecoNutriList = listOf("a","b","c","d","e")
    val novaList = listOf("1","2","3","4")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val barcode = intent.getStringExtra("EXTRA_BARCODE")
        var scannedImg = intent.getParcelableExtra<Bitmap>("EXTRA_PRODUCT_IMG")

        GlobalScope.launch(Dispatchers.IO) {
            val productInfo = find_product_info(barcode)
            withContext(Dispatchers.Main){
                if(scannedImg == null){
                    scannedImg = BitmapFactory.decodeResource(resources, R.drawable.missing_product_image)
                }
                binding.ivPhoto.setImageBitmap(scannedImg)
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

    fun displayProductInfo(product: Product?){
        product?.let { product ->
            if(product.status == 0){
                val img = BitmapFactory.decodeResource(resources, R.drawable.food_not_found)
                binding.ivPhoto.setImageBitmap(img)
            }
            else{
                val packaging = product.product
                processPackaging(packaging)
            }
        }
    }

    private fun processPackaging(packaging: Packaging) {

        val unknown = "unknown"
        val ecoscore = "ic_ecoscore_"
        val nutriscore = "ic_nutriscore_"
        val novascore = "ic_nova_group_"

        if (packaging.ecoscore_grade in ecoNutriList ){
            setScores("$ecoscore${packaging.ecoscore_grade}", binding.ivEcoScore)
        }
        else{
            setScores("$ecoscore$unknown", binding.ivEcoScore)
        }

        if (packaging.nutriscore_grade in ecoNutriList ){
            setScores("$nutriscore${packaging.nutriscore_grade}", binding.ivNutriScore)
        }
        else{
            setScores("$nutriscore$unknown", binding.ivNutriScore)
        }

        if (packaging.nova_group in novaList ){
            setScores("$novascore${packaging.nova_group}", binding.ivNovaScore)
        }
        else{
            setScores("$novascore$unknown", binding.ivNovaScore)
        }

    }

    private fun setScores(scoreString: String, imgView: ImageView, ){
        val context: Context = imgView.getContext()
        val id: Int = context.getResources().getIdentifier(scoreString, "drawable", context.getPackageName())
        imgView.setImageResource(id)
    }


}