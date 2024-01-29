package com.example.foodbook

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.InputStream

class UploadActivity : AppCompatActivity()
{
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var viewModel: AIModel
    private lateinit var bitMap: Bitmap     // store this to firebase

    private val REQUEST_IMAGE_CAPTURE = 101
    private val PICK_IMAGE_REQUEST = 102

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.geminiPrompt)

        viewModel = ViewModelProvider(this).get(AIModel::class.java)
        viewModel.textResult.observe(this, Observer { result ->
            textView.text = result
        })

        onClickListeners()
    }

    private fun onClickListeners()
    {
        val generateText = findViewById<Button>(R.id.buttonGenerateText)
        val captureButton = findViewById<Button>(R.id.captureButton)
        val chooseImageButton = findViewById<Button>(R.id.chooseImageButton)
        val uploadToFirebaseButton = findViewById<Button>(R.id.buttonUploadToFirebase)

        generateText.setOnClickListener {
            viewModel.generateText(bitMap)
        }

        captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        chooseImageButton.setOnClickListener {
            openFileChooser()
        }

        uploadToFirebaseButton.setOnClickListener {
            //UploadLogic("hello", "world")    // test to see if this will call only. to remove
            UploadFile("uploads")
        }
    }

    private fun openFileChooser()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/*")
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        // the result of taking picture using the camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            bitMap = imageBitmap
        }

        // the result of getting the picture from the file
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
            data != null && data.data != null)
        {
            val selectedImageUri: Uri? = data.data

            var inputStream: InputStream? = null
            if(selectedImageUri != null) {
                inputStream = contentResolver.openInputStream(selectedImageUri)
            }

            bitMap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitMap)
        }
    }

    public fun UploadFile(filepath: String)
    {
        val progressbar = findViewById<ProgressBar>(R.id.uploadProgressBar)
        val captiontext = findViewById<TextInputEditText>(R.id.postCaptionTextInputLayout)

        if(getImageURI() != null)
        {
            var uri: Uri? = getImageURI()
            val filepathString: String = filepath + System.currentTimeMillis() + "." + getFileExtension(UploadActivity().applicationContext, uri!!)

            println(filepathString)

            var lStorage: StorageReference = mStorageRef.child(filepathString)
            lStorage.putFile(uri!!)
                .addOnSuccessListener { taskSnapshot ->
                    lStorage.downloadUrl.addOnSuccessListener {downloaduri->
                        // println(downloaduri.toString())
                        val downloadURL: String = downloaduri.toString()
                        Toast.makeText(UploadActivity().application, "Upload successful", Toast.LENGTH_SHORT).show()
                        val uploadinst = UploadLogic(captiontext.text.toString().trim(), downloadURL)
                        val uploadID: String? = mDatabaseRef.push().key
                        mDatabaseRef.child(uploadID!!).setValue(uploadinst)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(UploadActivity().application, "ERROR: unable to upload file", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                    var uploadProgress: Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                    progressbar.setProgress(uploadProgress.toInt())
                }
        }
        else {
            Toast.makeText(UploadActivity().applicationContext, "No imagefile selected", Toast.LENGTH_SHORT).show()
        }
    }

}