package com.example.ecoconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity

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

    private fun getMatchString(tags: Set<String>, matchString: String): String {
        val builder = StringBuilder()
        builder.append(matchString)
        tags.forEach {
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

        tvStoreName.text = matchedTag.mDepositLocation.storeName
        tvUrlTitle.text = matchedTag.mDepositLocation.urlTitle
        tvUrl.text = matchedTag.mDepositLocation.url

        tvShapeMTags.text = getMatchString(matchedTag.mShapeTags, "Shape Matches: ")
        tvMaterialMTags.text = getMatchString(matchedTag.mMaterialTags, "Material Matches: ")
        tvCategoryTags.text = getMatchString(matchedTag.mCategoryTag, "Category Matches: ")
        tvMatchScore.text = "Score: ${matchedTag.mScore}"

        val locationURL = matchedTag.mDepositLocation.locationURL
        if(!locationURL.isEmpty()){
            val btnNearbyLocations = rowView.findViewById(R.id.btnNearbyLocations) as Button
            btnNearbyLocations.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(locationURL))
                startActivity(lvContext,intent,null)
            }
        }


        return rowView
    }

}