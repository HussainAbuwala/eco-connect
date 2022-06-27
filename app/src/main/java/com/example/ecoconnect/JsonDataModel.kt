package com.example.ecoconnect

import android.os.Parcelable

// Data Model For Getting Packaging Information
data class Product(val product: Packaging, val status: Int)

data class Packaging(val packagings: List<PackList>,
                     val nova_group: String,
                     val ecoscore_grade: String,
                     val nutriscore_grade: String,
                     val image_front_thumb_url: String,
                     val categories_tags: List<String>)

data class PackList(val material: String,
                    val recycling: String,
                    val shape: String)

data class DepositLocations(val url: String,
                            val urlTitle: String,
                            val storeName: String,
                            val shapeTags: ArrayList<String>,
                            val materialTags: ArrayList<String>,
                            val categoryTags: ArrayList<String>)

data class MatchedTags(
    var mShapeTags: Set<String>,
    val mMaterialTags: Set<String>,
    val mCategoryTag: Set<String>,
    val mDepositLocation: DepositLocations)

