package com.example.ecoconnect

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ecoconnect.databinding.ActivityUserInTheLoopBinding

class UserInTheLoopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInTheLoopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInTheLoopBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val shapeMatches = intent.getSerializableExtra("EXTRA_SHAPE" ) as ArrayList<String>
        val materialMatches = intent.getSerializableExtra( "EXTRA_MATERIAL" ) as ArrayList<String>
        val categoryMatches = intent.getSerializableExtra( "EXTRA_CATEGORY" ) as ArrayList<String>

        Log.d("Tags", shapeMatches.toString())
        Log.d("Tags", materialMatches.toString())
        Log.d("Tags", categoryMatches.toString())


        val shapeItems = getShapeArray()
        val materialItems = getMaterialArray()
        val categoryItems = getCategoryArray()

        val shapeBool = getBooleanItems(shapeItems,shapeMatches)
        val materialBool = getBooleanItems(materialItems,materialMatches)
        val categoryBool = getBooleanItems(categoryItems,categoryMatches)

        setDropDownMenuListeners(binding.txtShapeD,shapeItems,shapeBool,"Select Shapes")
        setDropDownMenuListeners(binding.txtMaterialD,materialItems,materialBool,"Select Materials")
        setDropDownMenuListeners(binding.txtCategoryD,categoryItems,categoryBool,"Select Categories")

        binding.button.setOnClickListener {

            val finalShapeItems = getSelectedItems(shapeItems,shapeBool)
            val finalMaterialItems = getSelectedItems(materialItems,materialBool)
            val finalCategoryItems = getSelectedItems(categoryItems, categoryBool)

            Intent(this,DepositLocationsActivity::class.java).also{
                it.putExtra("EXTRA_SHAPE",finalShapeItems)
                it.putExtra("EXTRA_MATERIAL",finalMaterialItems)
                it.putExtra("EXTRA_CATEGORY",finalCategoryItems)
                startActivity(it)
            }

        }

    }

    private fun getBooleanItems(items: Array<String>, matches: ArrayList<String>): BooleanArray {
        val boolItems = ArrayList<Boolean>()
        items.forEach{
            boolItems.add(false)
        }

        items.forEachIndexed { index, s ->
            if(s in matches){
                boolItems[index] = true
            }
        }
        return boolItems.toBooleanArray()
    }

    private fun getSelectedItems(items: Array<String>,boolItems: BooleanArray): ArrayList<String> {
        val finalCheckedItems = ArrayList<String>()
        boolItems.forEachIndexed { index, b ->
            if(b){
                finalCheckedItems.add(items[index])
            }
        }
        return finalCheckedItems
    }

    private fun setDropDownMenuListeners(txtView: TextView, items: Array<String>,
                                         checkedItems: BooleanArray, title: String  ){

        txtView.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            //val items = arrayOf("Black", "Orange", "Green", "Yellow")
            //val checkedItems = booleanArrayOf(true, false, false, true)

            builder.setTitle(title)

            builder.setMultiChoiceItems(
                items,
                checkedItems,
                DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->

                    checkedItems[which] = isChecked

                    val currentItem = items[which]
                    // Notify the current action
                    Toast.makeText(this, currentItem + " " + isChecked, Toast.LENGTH_SHORT).show()

                })

            builder.setPositiveButton("OK") { dialog, which ->
                // Do something when click positive button

                val stringBuilder = StringBuilder()

                for (i in checkedItems.indices) {
                    val checked = checkedItems[i]
                    if (checked) {
                        stringBuilder.append(items[i])
                        if(i != checkedItems.size - 1){
                            stringBuilder.append(", ")
                        }
                    }

                }

                txtView.text = stringBuilder.toString()
            }

            builder.setNeutralButton("Cancel") { dialog, which ->
                // Do something when click the neutral button
            }

            val dialog = builder.create()
            // Display the alert dialog on interface
            dialog.show()

        }

    }

    private fun getShapeArray(): Array<String> {
        val shapes = ArrayList<String>()
        Shape.values().forEach {
            shapes.add(it.value)
        }
        return shapes.toTypedArray()
    }

    private fun getMaterialArray(): Array<String> {
        val materials = ArrayList<String>()
        Material.values().forEach {
            materials.add(it.value)
        }
        return materials.toTypedArray()
    }

    private fun getCategoryArray(): Array<String> {
        val categorys = ArrayList<String>()
        Category.values().forEach {
            categorys.add(it.value)
        }
        return categorys.toTypedArray()
    }
}