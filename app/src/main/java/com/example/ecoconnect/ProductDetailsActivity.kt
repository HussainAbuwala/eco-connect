package com.example.ecoconnect

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.View
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.ecoconnect.databinding.ActivityProductDetailsBinding
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.coroutines.EmptyCoroutineContext.get


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
        val url = "https://world.openfoodfacts.org/api/v2/product/$barcode.json?fields=packagings,ecoscore_grade,nutriscore_grade,nova_group,categories_tags,image_front_thumb_url"
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
                if(!packaging.packagings.isEmpty()){
                    displayProductPackagingInfo(packaging)
                }
                displayProductScore(packaging)
                displayProductImage(packaging.image_front_thumb_url)
                binding.btnDepositLocation.setVisibility(View.VISIBLE);
                binding.btnDepositLocation.setOnClickListener {
                    sendTagInfo(packaging)
                }
            }
        }
    }

    private fun displayProductImage(imageFrontSmallUrl: String) {
        if(imageFrontSmallUrl == null){
            val scannedImg = BitmapFactory.decodeResource(resources, R.drawable.missing_product_image)
            binding.ivPhoto.setImageBitmap(scannedImg)
        }
        else{
            Picasso.with(this)
                .load(imageFrontSmallUrl)
                .fit()
                .into(binding.ivPhoto)
        }
        Log.d("displayProductImage","$imageFrontSmallUrl")
    }


    fun sendTagInfo(packaging: Packaging){

        val shape = ArrayList<String>()
        val material = ArrayList<String>()
        val category = ArrayList<String>()

        if(!packaging.packagings.isEmpty()){
            packaging.packagings.forEach { packList ->
                packList.shape?.let {
                    shape.add(it.removePrefix("en:"))
                }
                packList.material?.let {
                    material.add(it.removePrefix("en:"))
                }
            }
        }

        Log.d("sendTagInfo","$packaging")
        packaging.categories_tags?.let { cat_tags ->
            if(!cat_tags.isEmpty()){
                cat_tags.forEach {
                    category.add(it.removePrefix("en:"))
                }
            }
        }

        Intent(this,DepositLocationsActivity::class.java).also{
            it.putExtra("EXTRA_SHAPE",shape)
            it.putExtra("EXTRA_MATERIAL",material)
            it.putExtra("EXTRA_CATEGORY",category)
            startActivity(it)
        }

    }


    private fun displayProductPackagingInfo(packaging: Packaging){

        val tv1 = getTextView("Shape",Typeface.BOLD_ITALIC)
        val tv2 = getTextView("Material",Typeface.BOLD_ITALIC)
        val tv3 = getTextView("Recycling",Typeface.BOLD_ITALIC)

        val tblRow = getTblRow()
        tblRow.addView(tv1)
        tblRow.addView(tv2)
        tblRow.addView(tv3)

        binding.tlPackaging.addView(tblRow)

        packaging.packagings.forEach {

            val tv1 = getTextView(it.shape)
            val tv2 = getTextView(it.material)
            val tv3 = getTextView(it.recycling)

            val tblRow = getTblRow()
            tblRow.addView(tv1)
            tblRow.addView(tv2)
            tblRow.addView(tv3)

            binding.tlPackaging.addView(tblRow)
        }
    }

    private fun getTextView(content: String, typeface: Int = Typeface.NORMAL, padding:Int = 10, width:Int = 300, textSize:Float = 15F): TextView {
        val txtView = TextView(this)
        txtView.text = content
        txtView.setPadding(padding)
        txtView.width = width
        txtView.setTypeface(null, typeface)
        txtView.textSize = textSize
        return txtView
    }

    private fun getTblRow(): TableRow {
        val tblRow = TableRow(this)
        tblRow.gravity = CENTER_HORIZONTAL
        return tblRow
    }


    private fun displayProductScore(packaging: Packaging) {

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

    private fun setScores(scoreString: String, imgView: ImageView){
        val context: Context = imgView.getContext()
        val id: Int = context.getResources().getIdentifier(scoreString, "drawable", context.getPackageName())
        imgView.setImageResource(id)
    }


}