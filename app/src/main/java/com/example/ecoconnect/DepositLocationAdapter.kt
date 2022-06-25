package com.example.ecoconnect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class DepositLocationAdapter(context: Context, dataSource: ArrayList<DepositLocations>) : BaseAdapter() {

    private val lvContext: Context
    private val lvDataSource: ArrayList<DepositLocations>
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.row_deposit_location, parent, false)
        val depositLocation = getItem(position) as DepositLocations
        val tvStoreName = rowView.findViewById(R.id.tvStoreName) as TextView
        val tvUrlTitle = rowView.findViewById(R.id.tvUrlTitle) as TextView
        val tvUrl = rowView.findViewById(R.id.tvUrl) as TextView

        tvStoreName.text = depositLocation.storeName
        tvUrlTitle.text = depositLocation.urlTitle
        tvUrl.text = depositLocation.url

        return rowView
    }

}