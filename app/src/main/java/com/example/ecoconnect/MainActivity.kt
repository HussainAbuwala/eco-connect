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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnTakePhoto.setOnClickListener {
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
            setScannedImage(scannedImg)
            readBarcode(scannedImg)
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
                    setBarcode("No Barcode Found")
                    Log.d("getImgBarcode","No barcode found")
                }
                else{
                    barcodes[0]?.rawValue?.let { setBarcode(it) }
                }
            }
            .addOnFailureListener {
                Log.d("getImgBarcode","Barcode Reader Failure")
            }
    }


    private fun setBarcode(barcode: String){
        binding.tvScanResult.text = "Product Barcode: $barcode"
    }

    private fun setScannedImage(scannedImg: Bitmap){
        binding.ivPhoto.setImageBitmap(scannedImg)
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