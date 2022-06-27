package com.example.ecoconnect

import android.R.attr.country
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ecoconnect.databinding.ActivityDepositLocationsBinding
import com.google.gson.Gson
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


    private fun buildMatchedTag(depositLocation: DepositLocations, mTags: Triple<MutableSet<String>, MutableSet<String>, MutableSet<String>>): MatchedTags {
        val matchedTag = MatchedTags(mTags.first, mTags.second, mTags.third, depositLocation)
        return matchedTag
    }

    private fun getMatchScore(locationInfo: DepositLocations,
                              p_shape_tags: ArrayList<String>,
                              p_material_tags: ArrayList<String>,
                              p_category_tags: ArrayList<String>): Triple<MutableSet<String>, MutableSet<String>, MutableSet<String>> {

        val shapeMatches = mutableSetOf<String>()
        findMatch(locationInfo.shapeTags, p_shape_tags,shapeMatches)

        val materialMatches = mutableSetOf<String>()
        findMatch(locationInfo.materialTags, p_material_tags,materialMatches)

        val categoryMatches = mutableSetOf<String>()
        findMatch(locationInfo.categoryTags, p_category_tags,categoryMatches)


        Log.d("getMatchScore",shapeMatches.toString())
        Log.d("getMatchScore",materialMatches.toString())
        Log.d("getMatchScore",categoryMatches.toString())

        return Triple(shapeMatches,materialMatches,categoryMatches)

    }

    private fun findMatch(locationTags: ArrayList<String>, productTags: ArrayList<String>, shapeMatches: MutableSet<String>): MutableSet<String> {

        val matches = mutableSetOf<String>()

        if(!locationTags.isEmpty() && productTags.isEmpty()){
            locationTags.forEach { sTD ->
                productTags.forEach { sTP ->
                    if(sTP.contains(sTD,true)){
                        matches.add(sTD)
                    }
                }
            }
        }

        return matches
    }


    private fun buildDepositLocation(url: String, storeName: String): DepositLocations {

        val doc = Jsoup.connect(url).get()
        val shape = ArrayList<String>()
        val material = ArrayList<String>()
        val category = ArrayList<String>()
        val title = doc.select("title").text()

        Shape.values().forEach {
            val matchedElements = doc.select(":containsOwn(${it.value})")
            if(!matchedElements.isEmpty()){
                shape.add(it.value)
            }
        }

        Material.values().forEach {
            val matchedElements = doc.select(":containsOwn(${it.value})")
            if(!matchedElements.isEmpty()){
                material.add(it.value)
            }
        }

        Category.values().forEach {
            val matchedElements = doc.select(":containsOwn(${it.value})")
            if(!matchedElements.isEmpty()){
                category.add(it.value)
            }
        }

        Log.d("DepositLocationsActivit",shape.toString())
        Log.d("DepositLocationsActivit",material.toString())
        Log.d("DepositLocationsActivit",category.toString())


        val depositLocation = DepositLocations(url,title,storeName,shape,material,category)


        return depositLocation

    }

    private fun saveDepositLocations(){

        val sharedPref = getSharedPreferences("depositLocationPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        // country is an enum.
        // country is an enum.
        val json_country = gson.toJson(country)
        editor.apply {

        }
    }

}