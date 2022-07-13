package com.example.ecoconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ecoconnect.databinding.ActivityDepositLocationsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.reflect.Type


class DepositLocationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDepositLocationsBinding
    private val stores = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositLocationsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val shape = intent.getSerializableExtra("EXTRA_SHAPE" ) as ArrayList<String>
        val material = intent.getSerializableExtra( "EXTRA_MATERIAL" ) as ArrayList<String>
        val category = intent.getSerializableExtra( "EXTRA_CATEGORY" ) as ArrayList<String>
        var dataSource = ArrayList<MatchedTags>()

        GlobalScope.launch(Dispatchers.IO) {

            readLocationURLs(stores)
            buildDataSource(stores,shape,material,category,dataSource)
            dataSource.sortByDescending { it.mScore }

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

    private fun readLocationURLs(stores: MutableList<String>){
        assets.open("urls").bufferedReader().forEachLine {
            stores.add(it)
        }
    }

    private fun getStoreName(url: String): String {
        val start_index = url.indexOf('.')
        val end_index = url.indexOf('.',start_index + 1)
        return url.substring(start_index + 1,end_index)
    }

    private fun buildDataSource(stores: MutableList<String>,
                                shape: ArrayList<String>,
                                material: ArrayList<String>,
                                category: ArrayList<String>,
                                dataSource:ArrayList<MatchedTags>){

        stores.forEach {
            val line = it.trim().split(",").toMutableList()
            val storeURL = line[0]
            val locationURL = line[1]
            val storeName = getStoreName(it)
            val depositLocation = buildDepositLocation(storeURL, storeName, locationURL)
            val mScore = getMatchScore(depositLocation, shape, material, category)
            val matchTag = buildMatchedTag(depositLocation,mScore)
            dataSource.add(matchTag)
        }
    }


    private fun buildMatchedTag(depositLocation: DepositLocations, mTags: Triple<MutableSet<String>, MutableSet<String>, MutableSet<String>>): MatchedTags {
        val score = mTags.first.size + mTags.second.size + mTags.third.size
        val matchedTag = MatchedTags(mTags.first, mTags.second, mTags.third, score, depositLocation)
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

        return Triple(shapeMatches,materialMatches,categoryMatches)

    }

    private fun findMatch(locationTags: ArrayList<String>, productTags: ArrayList<String>, matches: MutableSet<String>){

        if(!locationTags.isEmpty() && !productTags.isEmpty()){
            locationTags.forEach { sTD ->
                productTags.forEach { sTP ->
                    if(sTP.contains(sTD,true)){
                        matches.add(sTD)
                    }
                }
            }
        }
    }


    private fun buildDepositLocation(storeURL: String, storeName: String, storeLocationURL: String): DepositLocations {

        val doc = Jsoup.connect(storeURL).get()
        val title = doc.select("title").text()

        val s_p = getSharedPreferences(storeName, MODE_PRIVATE)
        if(s_p.contains("SHAPE_TAGS")){
            Log.d("buildDepositLocation","ffll")
            val(shape,material,category) = loadDepositLocations(storeName)
            return DepositLocations(storeURL,title,storeName,storeLocationURL,shape,material,category)
        }

        val shape = ArrayList<String>()
        val material = ArrayList<String>()
        val category = ArrayList<String>()


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

        val depositLocation = DepositLocations(storeURL,title,storeName,storeLocationURL,shape,material,category)
        saveDepositLocations(depositLocation)
        return depositLocation

    }

    private fun saveDepositLocations(depositLocations: DepositLocations){

        val sharedPref = getSharedPreferences(depositLocations.storeName, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        val shape_tags_json = gson.toJson(depositLocations.shapeTags)
        val material_tags_json = gson.toJson(depositLocations.materialTags)
        val category_tags_json = gson.toJson(depositLocations.categoryTags)

        editor.apply {
            putString("SHAPE_TAGS",shape_tags_json)
            putString("MATERIAL_TAGS",material_tags_json)
            putString("CATEGORY_TAGS",category_tags_json)
            apply()
        }

    }

    private fun loadDepositLocations(storeName: String): Triple<ArrayList<String>, ArrayList<String>, ArrayList<String>> {

        val sharedPreferences = getSharedPreferences(storeName, MODE_PRIVATE)

        val gson = Gson()

        val shape_json = sharedPreferences.getString("SHAPE_TAGS", null)
        val material_json = sharedPreferences.getString("MATERIAL_TAGS", null)
        val category_json = sharedPreferences.getString("CATEGORY_TAGS", null)

        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type

        var shapeTagArrayList = gson.fromJson<ArrayList<String>>(shape_json, type)
        var materialTagArrayList = gson.fromJson<ArrayList<String>>(material_json, type)
        var categoryTagArrayList = gson.fromJson<ArrayList<String>>(category_json, type)


        if (shapeTagArrayList == null) {
            shapeTagArrayList = ArrayList<String>()
        }
        if (materialTagArrayList == null) {
            materialTagArrayList = ArrayList<String>()
        }
        if (categoryTagArrayList == null) {
            categoryTagArrayList = ArrayList<String>()
        }

        return Triple(shapeTagArrayList,materialTagArrayList,categoryTagArrayList)

    }

}