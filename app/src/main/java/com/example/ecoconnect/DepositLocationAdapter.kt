package com.example.ecoconnect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class DepositLocationAdapter(context: Context, dataSource: ArrayList<MatchedTags>) : BaseAdapter() {

    private val lvContext: Context
    private val lvDataSource: ArrayList<MatchedTags>
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    init {
        lvContext = context
        lvDataSource = dataSource
    }

    override fun getCount(): Int {
        return lvDataSource.size
    }

    override fun getItem(position: Int): Any {
        return lvDataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun getShapeString(shapeTags: Set<Shape>): String {
        val builder = StringBuilder()
        builder.append("Shape Matches: ")
        shapeTags.forEach {
            builder.append("$it, ")
        }
        return builder.toString()
    }

    private fun getMaterialString(materialTags: Set<Material>): String {
        val builder = StringBuilder()
        builder.append("Material Matches: ")
        materialTags.forEach {
            builder.append("$it, ")
        }
        return builder.toString()
    }

    private fun getCategoryString(categoryTags: Set<Category>): String {
        val builder = StringBuilder()
        builder.append("Category Matches: ")
        categoryTags.forEach {
            builder.append("$it, ")
        }
        return builder.toString()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.row_deposit_location, parent, false)
        val matchedTag = getItem(position) as MatchedTags

        val tvStoreName = rowView.findViewById(R.id.tvStoreName) as TextView
        val tvUrlTitle = rowView.findViewById(R.id.tvUrlTitle) as TextView
        val tvUrl = rowView.findViewById(R.id.tvUrl) as TextView

        val tvShapeMTags = rowView.findViewById(R.id.tvShapeMTags) as TextView
        val tvMaterialMTags =  rowView.findViewById(R.id.tvMaterialMTags) as TextView
        val tvCategoryTags =  rowView.findViewById(R.id.tvCategoryTags) as TextView
        val tvMatchScore = rowView.findViewById(R.id.tvMatchScore) as TextView
        val score = matchedTag.mShapeTags.size + matchedTag.mMaterialTags.size + matchedTag.mCategoryTag.size

        tvStoreName.text = matchedTag.mDepositLocation.storeName
        tvUrlTitle.text = matchedTag.mDepositLocation.urlTitle
        tvUrl.text = matchedTag.mDepositLocation.url

        tvShapeMTags.text = getShapeString(matchedTag.mShapeTags)
        tvMaterialMTags.text = getMaterialString(matchedTag.mMaterialTags)
        tvCategoryTags.text = getCategoryString(matchedTag.mCategoryTag)
        tvMatchScore.text = "Score: $score"

        return rowView
    }

}