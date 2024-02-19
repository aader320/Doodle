package com.example.foodbook

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.foodbook.Activities.HomepageActivity
import com.example.foodbook.Activities.UploadActivity

class AI : AppCompatActivity() {
    lateinit var viewModel: AIModel
    lateinit var bitMap : Bitmap
    lateinit var upload : UploadActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai)

        findViewById<Button>(R.id.button2).setOnClickListener {
            viewModel.generateText(bitMap,"What's this food item?")
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            viewModel.generateText(bitMap,"What's the history about this food item?")
        }

        findViewById<Button>(R.id.button1).setOnClickListener {
            viewModel.generateText(bitMap,"Where's the best place to eat this food item in Singapore?")
        }

        findViewById<Button>(R.id.button4).setOnClickListener {
            viewModel.generateText(bitMap,"What's this item?")
        }
    }
}
