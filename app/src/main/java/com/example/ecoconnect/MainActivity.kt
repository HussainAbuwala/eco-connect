package com.example.ecoconnect

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ecoconnect.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class MainActivity : AppCompatActivity() {

    val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val camera_intent_registered = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        onCameraIntentResult(it)
    }
    private lateinit var binding: ActivityMainBinding
    private val BARCODE_SCAN_INTENT_TAG = 1
    private val OBJECT_DETECT_INTENT_TAG = 2
    private var CURRENT_INTENT_TAG = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setBtnListener(binding.btnScanBarcode,BARCODE_SCAN_INTENT_TAG)
        setBtnListener(binding.btnDetectObject,OBJECT_DETECT_INTENT_TAG)

    }

    private fun setBtnListener(btn: Button, intent_tag: Int){
        btn.setOnClickListener {
            CURRENT_INTENT_TAG = intent_tag
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                camera_intent_registered.launch(camera_intent)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
    }

    private fun onCameraIntentResult(result: ActivityResult?) {
        if(result?.resultCode == Activity.RESULT_OK){
            val scannedImg: Bitmap = result.data!!.extras!!.get("data") as Bitmap
            if(CURRENT_INTENT_TAG == BARCODE_SCAN_INTENT_TAG){
                readBarcode(scannedImg)
            }
            else{
                sendObjectImage(scannedImg)
            }
        }
        else{
            Log.d("onCameraIntentResult","Camera Intent Failed")
        }
    }

    private fun readBarcode(scannedImg: Bitmap){

        val options = BarcodeScannerOptions.Builder().build()
        val image = InputImage.fromBitmap(scannedImg, 0)
        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if(barcodes.isEmpty()){
                    Toast.makeText(this,"Barcode Not Found, Please Scan Again", Toast.LENGTH_LONG).show()
                }
                else{
                    barcodes[0]?.rawValue?.let {
                        sendProductDetails(it,scannedImg)
                    }
                }
            }
            .addOnFailureListener {
                Log.d("getImgBarcode","Barcode Reader Failure")
            }
    }

    private fun sendObjectImage(scannedImg: Bitmap){
        Intent(this,ObjectDetectActivity::class.java).also{
            it.putExtra("EXTRA_PRODUCT_IMG",scannedImg)
            startActivity(it)
        }
    }

    private fun sendProductDetails(barcode: String, scannedImg: Bitmap){
        Intent(this,ProductDetailsActivity::class.java).also{
            it.putExtra("EXTRA_PRODUCT_IMG",scannedImg)
            it.putExtra("EXTRA_BARCODE",barcode)
            startActivity(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            camera_intent_registered.launch(camera_intent)
        }
        else{
            Toast.makeText(this,"You denied the Camera Permission", Toast.LENGTH_LONG).show()
        }
    }


}