package com.example.foodbook.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.foodbook.R
import com.google.android.material.textfield.TextInputEditText

class GpsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_gps)

        val findbutton = findViewById<Button>(R.id.findButton)
        findbutton.setOnClickListener()
        {
            openGoogleMaps()
        }
    }

    private fun openGoogleMaps()
    {
        val location = findViewById<TextInputEditText>(R.id.locationInput)

        val gmmIntentUri: Uri = Uri.parse("geo:0,0?q=${location.text}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val output = findViewById<TextView>(R.id.resultText)
            output.text = "Please Install Google Maps"
        }
    }
}