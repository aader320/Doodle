package com.example.foodbook

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

object BitmapHolder {
    var bitmap: Bitmap? = null
}

class AI : AppCompatActivity() {
    lateinit var viewModel: AIModel
    lateinit var bitMap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai)

        if (BitmapHolder.bitmap != null)
        {
            bitMap = BitmapHolder.bitmap!!
        }

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
