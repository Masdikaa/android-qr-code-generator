package com.masdika.qrcodegenerator.fragment

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.masdika.qrcodegenerator.databinding.FragmentGenerateBinding
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Calendar

class GenerateFragment : Fragment() {

    private var _binding: FragmentGenerateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGenerateBinding.inflate(inflater, container, false)

        var textInput: String
        var counter = 0

        binding.btnGenerate.setOnClickListener {
            textInput = binding.inputText.text.toString()
            if (textInput.isEmpty()) {
                binding.inputText.error = "Field is still empty !"
                binding.inputText.requestFocus()
            } else {
                generateQRCode(textInput)
                UIUtil.hideKeyboard(activity) //Hide soft keyboard
                counter++
            }
        }

        binding.btnDownload.setOnClickListener {
            if (counter != 0) {
                val bitmap = getImageOfView(binding.imageQrResult)
                saveToStorage(bitmap as Bitmap)
            } else {
                Toast.makeText(context, "Generate text first", Toast.LENGTH_SHORT).show()
                binding.inputText.requestFocus()
            }
        }

        return binding.root
    }

    private fun generateQRCode(input: String) {
        val writer = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix =
                writer.encode(input, BarcodeFormat.QR_CODE, 1000, 1000)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            binding.imageQrResult.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.e("Error", e.toString())
        }
    }

    private fun saveToStorage(bitmap: Bitmap) {
        val imageName = "QR_${System.currentTimeMillis()}_${Calendar.YEAR}.jpg"
        var fos: OutputStream?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity?.contentResolver.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
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
            Toast.makeText(context, "Succesfully saved to gallery", Toast.LENGTH_SHORT).show()
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