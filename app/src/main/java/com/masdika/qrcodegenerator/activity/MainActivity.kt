package com.masdika.qrcodegenerator.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var textInput: String

        binding.btnGenerate.setOnClickListener {
            textInput = binding.inputText.text.toString()
            val writer: MultiFormatWriter = MultiFormatWriter()

            if (textInput.isEmpty()) {
                binding.inputText.error = "Field belum di isi!"
                binding.inputText.requestFocus()
            } else {

                try {
                    val bitMatrix: BitMatrix =
                        writer.encode(textInput, BarcodeFormat.QR_CODE, 1000, 1000)
                    val barcodeEncoder: BarcodeEncoder = BarcodeEncoder()
                    val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

                    binding.imageQrResult.setImageBitmap(bitmap)

                } catch (e: WriterException) {
                    Log.e("Error", e.toString())
                }
            }
        }

    }
}