package com.example.ecoconnect

import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecoconnect.databinding.ActivityObjectDetectBinding
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions


class ObjectDetectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityObjectDetectBinding
    private val categoryObjectDetect = mutableListOf<String>("cardboard", "glass", "metal", "paper", "plastic", "trash")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectDetectBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var scannedImg = intent.getParcelableExtra<Bitmap>("EXTRA_PRODUCT_IMG")
        binding.ivPhoto.setImageBitmap(scannedImg)
        scannedImg?.let {
            detectObject(scannedImg)
        }

    }

    private fun detectObject(scannedImg: Bitmap){

        val localModel = LocalModel.Builder()
            .setAssetFilePath("model.tflite")
            .build()

        val customObjectDetectorOptions =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableClassification()
                .setClassificationConfidenceThreshold(0.0f)
                .setMaxPerObjectLabelCount(3)
                .build()

        val objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)

        val image = InputImage.fromBitmap(scannedImg, 0)

        objectDetector.process(image).addOnSuccessListener { results ->
            if(!results.isEmpty()){
                displayClassifyResults(results)
            }
            else{
                setBtnListener(null)
            }
        }
    }

    private fun displayClassifyResults(results: List<DetectedObject>) {
        val detectedObject = results[0]
        val allConfidences = ArrayList<Pair<Int,Float>>()
        for (label in detectedObject.labels) {
            val text = label.text
            val index = label.index
            val confidence = label.confidence
            allConfidences.add(Pair(index,confidence))
            setProgressBarValue(index,confidence)

        }

        val materialMatch = categoryObjectDetect[allConfidences[0].first]
        setBtnListener(materialMatch)

    }

    private fun setBtnListener(materialMatch:String?){
        binding.btnObjDetectNext.setOnClickListener{
            sendObjectDetectInfo(materialMatch)
        }
    }

    private fun sendObjectDetectInfo(material : String?){

        val shapeMatches = ArrayList<String>()
        val materialMatches = ArrayList<String>()
        if (material != null) {
            materialMatches.add(material)
        }
        val categoryMatches = ArrayList<String>()

        Intent(this,UserInTheLoopActivity::class.java).also{
            it.putExtra("EXTRA_SHAPE",shapeMatches)
            it.putExtra("EXTRA_MATERIAL",materialMatches)
            it.putExtra("EXTRA_CATEGORY",categoryMatches)
            startActivity(it)
        }
    }

    private fun setProgressBarValue(label_index: Int, label_confidence: Float){
        val progressBarId: Int = resources.getIdentifier("progressBar${label_index}", "id", packageName)
        val progressBar = findViewById<View>(progressBarId) as ProgressBar
        progressBar.max = 100
        progressBar.progress = ((label_confidence * 100).toInt())
    }


}