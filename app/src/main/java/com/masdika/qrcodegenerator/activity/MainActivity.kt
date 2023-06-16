package com.masdika.qrcodegenerator.activity

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.masdika.qrcodegenerator.R
import com.masdika.qrcodegenerator.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue_6)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var textInput: String
        var counter = 0

        binding.btnGenerate.setOnClickListener {
            textInput = binding.inputText.text.toString()
            val writer = MultiFormatWriter()

            if (textInput.isEmpty()) {
                binding.inputText.error = "field is still empty !"
                binding.inputText.requestFocus()
            } else {
                try {
                    val bitMatrix: BitMatrix =
                        writer.encode(textInput, BarcodeFormat.QR_CODE, 1000, 1000)
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
                    binding.imageQrResult.setImageBitmap(bitmap)
                    counter++
                } catch (e: WriterException) {
                    Log.e("Error", e.toString())
                }
            }
        }

        binding.btnDownload.setOnClickListener {
            if (counter != 0) {
                val bitmap = getImageOfView(binding.imageQrResult)
                saveToStorage(bitmap as Bitmap)
            } else {
                Toast.makeText(this, "Generate text first", Toast.LENGTH_SHORT).show()
                binding.inputText.requestFocus()
            }
        }
    }

    private fun saveToStorage(bitmap: Bitmap) {
        val imageName = "QR_${System.currentTimeMillis()}_${Calendar.YEAR}.jpg"
        var fos: OutputStream?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let {
                    resolver.openOutputStream(it)
                }
            }
        } else {
            val imagesDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDirectory, imageName)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Succesfully saved to gallery", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageOfView(view: ImageView): Any {
        var image: Bitmap? = null
        try {
            image = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(image)
            view.draw(canvas)
        } catch (e: Exception) {
            Log.e("Error", "$e.toString()")
        }
        return image!!
    }

}