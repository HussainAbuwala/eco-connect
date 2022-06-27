package com.example.ecoconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ecoconnect.databinding.ActivityDepositLocationsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class DepositLocationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDepositLocationsBinding
    private val BEER_STORE_URL = "https://www.thebeerstore.ca/about-us/environmental-leadership/bag-it-back-odrp/"
    private val LCBO_STORE_URL = "https://www.lcbo.com/content/lcbo/en/corporate-pages/faq.html"
    private val stores = mutableListOf(Pair(BEER_STORE_URL,"The BEER Store"),
                                        Pair(LCBO_STORE_URL,"LCBO"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositLocationsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val shape = intent.getSerializableExtra("EXTRA_SHAPE" ) as ArrayList<String>
        val material = intent.getSerializableExtra( "EXTRA_MATERIAL" ) as ArrayList<String>
        val category = intent.getSerializableExtra( "EXTRA_CATEGORY" ) as ArrayList<String>
        val dataSource = ArrayList<MatchedTags>()

        Log.d("DepositLocationsActivit", shape.toString() )
        Log.d("DepositLocationsActivit", material.toString() )
        Log.d("DepositLocationsActivit", category.toString() )

        GlobalScope.launch(Dispatchers.IO) {

            buildDataSource(stores,shape,material,category,dataSource)

            withContext(Dispatchers.Main){
                binding.lvDepositLocations.adapter = DepositLocationAdapter(this@DepositLocationsActivity,dataSource)
                binding.lvDepositLocations.setOnItemClickListener { parent, view, position, id ->
                    val mTag = dataSource[position]
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mTag.mDepositLocation.url))
                    startActivity(intent)
                }
            }
        }

    }

    private fun buildDataSource(stores: MutableList<Pair<String,String>>,
                                shape: ArrayList<String>,
                                material: ArrayList<String>,
                                category: ArrayList<String>,
                                dataSource:ArrayList<MatchedTags>){

        stores.forEach {
            val depositLocation = buildDepositLocation(it.first, it.second)
            val mScore = getMatchScore(depositLocation, shape, material, category)
            val matchTag = buildMatchedTag(depositLocation,mScore)
            dataSource.add(matchTag)
        }
    }


    private fun buildMatchedTag(depositLocation: DepositLocations, mTags: Triple<MutableSet<Shape>, MutableSet<Material>, MutableSet<Category>>): MatchedTags {
        val matchedTag = MatchedTags(mTags.first, mTags.second, mTags.third, depositLocation)
        return matchedTag
    }

    private fun getMatchScore(locationInfo: DepositLocations, p_shape_tags: ArrayList<String>,
                              p_material_tags: ArrayList<String>, p_category_tags: ArrayList<String>): Triple<MutableSet<Shape>, MutableSet<Material>, MutableSet<Category>> {

        val shapeMatches = mutableSetOf<Shape>()
        if(!locationInfo.shapeTags.isEmpty() && !p_shape_tags.isEmpty()){
            locationInfo.shapeTags.forEach { sTD ->
                p_shape_tags.forEach { sTP ->
                    if(sTP.contains(sTD.value,true)){
                        shapeMatches.add(sTD)
                    }
                }
            }
        }

        val materialMatches = mutableSetOf<Material>()
        if(!locationInfo.materialTags.isEmpty() && !p_material_tags.isEmpty()){
            locationInfo.materialTags.forEach { sTD ->
                p_material_tags.forEach { sTP ->
                    if(sTP.contains(sTD.value,true)){
                        materialMatches.add(sTD)
                    }
                }
            }
        }

        val categoryMatches = mutableSetOf<Category>()
        if(!locationInfo.categoryTags.isEmpty() && !p_category_tags.isEmpty()){
            locationInfo.categoryTags.forEach { sTD ->
                p_category_tags.forEach { sTP ->
                    if(sTP.contains(sTD.value,true)){
                        categoryMatches.add(sTD)
                    }
                }
            }
        }

        Log.d("getMatchScore",shapeMatches.toString())
        Log.d("getMatchScore",materialMatches.toString())
        Log.d("getMatchScore",categoryMatches.toString())

        return Triple(shapeMatches,materialMatches,categoryMatches)

    }


    private fun buildDepositLocation(url: String, storeName: String): DepositLocations {

        val doc = Jsoup.connect(url).get()
        val shape = ArrayList<Shape>()
        val material = ArrayList<Material>()
        val category = ArrayList<Category>()
        val title = doc.select("title").text()

        Shape.values().forEach {
            val matchedElements = doc.select(":containsOwn(${it.value})")
            if(!matchedElements.isEmpty()){
                shape.add(it)
            }
        }

        Material.values().forEach {
            val matchedElements = doc.select(":containsOwn(${it.value})")
            if(!matchedElements.isEmpty()){
                material.add(it)
            }
        }

        Category.values().forEach {
            val matchedElements = doc.select(":containsOwn(${it.value})")
            if(!matchedElements.isEmpty()){
                category.add(it)
            }
        }

        Log.d("DepositLocationsActivit",shape.toString())
        Log.d("DepositLocationsActivit",material.toString())
        Log.d("DepositLocationsActivit",category.toString())


        val depositLocation = DepositLocations(url,title,storeName,shape,material,category)
        return depositLocation

    }
}