package com.example.foodbook.Activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.foodbook.AIModel
import com.example.foodbook.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storageMetadata
import java.io.ByteArrayOutputStream
import java.io.InputStream

class UploadActivity : AppCompatActivity()
{
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var viewModel: AIModel
    private lateinit var bitMap: Bitmap     // store this to firebase
    private lateinit var userEmail: String

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

        Places.initialize(applicationContext, "AIzaSyAiaPMS-yV8eKDHSLipnAypwshfVd0kWog")
        val placesClient: PlacesClient = Places.createClient(this)

        userEmail = intent.getStringExtra("USER_EMAIL").toString()
    }

    private fun onClickListeners()
    {
        val generateText = findViewById<Button>(R.id.buttonGenerateText)
        val captureButton = findViewById<Button>(R.id.captureButton)
        val chooseImageButton = findViewById<Button>(R.id.chooseImageButton)
        val uploadToFirebaseButton = findViewById<Button>(R.id.buttonUploadToFirebase)
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT)
        val resultLatLng = findViewById<TextView>(R.id.resultText)
        val resultLocation = findViewById<TextView>(R.id.resultName)
        val homepagebutton = findViewById<Button>(R.id.homepageButton)

        homepagebutton.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }

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
            //val filepathString: String = "uploads/" + intent.getStringExtra("USER_EMAIL")
            val filepathString: String = "uploads/" + userEmail
            val lStorage: StorageReference = FirebaseStorage.getInstance().getReference(filepathString)
            UploadFile(lStorage)
        }

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(LatLng(-33.0, 151.0), LatLng(-33.0, 152.0)))
        autocompleteFragment.setCountries("SG")
        autocompleteFragment.setPlaceFields(listOf(
            Place.Field.ID, Place.Field.NAME,
            Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                resultLatLng.text = "${place.latLng}"
                resultLatLng.text = resultLatLng.text.toString().replace("lat/lng: (", "").replace(")", "")
                resultLocation.text = "${place.name}"
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "An error occurred: ${status}")
            }
        })
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

    public fun UploadFile(Storage: StorageReference)
    {
        val progressbar = findViewById<ProgressBar>(R.id.uploadProgressBar)
        val captiontext = findViewById<TextInputLayout>(R.id.postCaptionTextInputLayout)
        val caption:String = captiontext.editText!!.text.toString()
        val resultLatLng = findViewById<TextView>(R.id.resultText)
        val GPSresult:String = resultLatLng.text.toString()

        if(caption.isEmpty()) {
            Toast.makeText(this, "Please Input a caption!", Toast.LENGTH_SHORT).show()
            return
        }

        if(GPSresult.isEmpty()) {
            Toast.makeText(this, "Please choose a location!", Toast.LENGTH_SHORT).show()
            return
        }

        val filepathString: String = System.currentTimeMillis().toString() + ".jpeg"
        val lStorage: StorageReference = Storage.child(filepathString)

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val baos = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val metadata = storageMetadata {
            contentType = "image/jpg"
            setCustomMetadata("Caption", caption)
            setCustomMetadata("Location", GPSresult)
            setCustomMetadata("User_Email", userEmail)
            setCustomMetadata("TimeSinceEpoch", System.currentTimeMillis().toString())
        }

        val Imagedata = baos.toByteArray()
        var uploadImage = lStorage.putBytes(Imagedata, metadata)

        uploadImage.addOnFailureListener {
            Toast.makeText(this, "UNSUCCESSFUL UPLOAD", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot->
            Toast.makeText(this, "UPLOAD SUCCESSFUL", Toast.LENGTH_SHORT).show()
        }
        .addOnProgressListener { snapshot->
            progressbar.setProgress((snapshot.bytesTransferred / snapshot.totalByteCount * 100).toInt())
        }
    }
}