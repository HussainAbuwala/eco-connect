package com.example.ecoconnect

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ecoconnect.databinding.ActivityMainBinding
import com.example.ecoconnect.databinding.ActivityObjectDetectBinding
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class ObjectDetectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityObjectDetectBinding

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
            displayClassifyResults(results)
        }
    }

    private fun displayClassifyResults(results: List<DetectedObject>) {
        val detectedObject = results[0]
        val s = 'a'
        val boundingBox = detectedObject.boundingBox
        val trackingId = detectedObject.trackingId
        for (label in detectedObject.labels) {

            if(label.index == 0){
                binding.progressBar0.max = 100
                binding.progressBar0.progress = ((label.confidence * 100).toInt())
            }
            if(label.index == 1){
                binding.progressBar1.max = 100
                binding.progressBar1.progress = ((label.confidence * 100).toInt())
            }
            if(label.index == 2){
                binding.progressBar2.max = 100
                binding.progressBar2.progress = ((label.confidence * 100).toInt())
            }
            if(label.index == 3){
                binding.progressBar3.max = 100
                binding.progressBar3.progress = ((label.confidence * 100).toInt())
            }
            if(label.index == 4){
                binding.progressBar4.max = 100
                binding.progressBar4.progress = ((label.confidence * 100).toInt())
            }
            if(label.index == 5){
                binding.progressBar5.max = 100
                binding.progressBar5.progress = ((label.confidence * 100).toInt())
            }

            val text = label.text
            val index = label.index
            val confidence = label.confidence
            Log.d("CLASSIFICATION","text: $text, index: $index, confidence: $confidence")
        }
    }


}