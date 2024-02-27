package com.example.foodbook

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

object BitmapHolder {
    var bitmap: Bitmap? = null
}

class AI : AppCompatActivity() {
    lateinit var viewModel: AIModel
    lateinit var bitMap : Bitmap
    lateinit var aiReply : TextView
    lateinit var prompt : TextView
    lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai)

        aiReply = findViewById(R.id.aiReply)
        prompt = findViewById(R.id.prompt)
        
        viewModel = ViewModelProvider(this).get(AIModel::class.java)
        viewModel.textResult.observe(this, Observer { result ->
            aiReply.text = result
        })

        editText = findViewById(R.id.inputAIText)

        if (BitmapHolder.bitmap != null)
        {
            bitMap = BitmapHolder.bitmap!!
        }

        findViewById<Button>(R.id.askInput).setOnClickListener {
            resetText()
            val tempString : String = editText.text.toString()
            viewModel.generateText(bitMap, tempString)
            editText.setText("")
        }

        findViewById<Button>(R.id.about).setOnClickListener {
            resetText()
            viewModel.generateText(bitMap,"Tell me about this food.")
        }

        findViewById<Button>(R.id.history).setOnClickListener {
            resetText()
            viewModel.generateText(bitMap,"What's the history of this food?")
        }

        findViewById<Button>(R.id.recipe).setOnClickListener {
            resetText()
            viewModel.generateText(bitMap,"What's the recipe for this food?")
        }
    }

    private fun resetText() {
        aiReply.text = "Loading..."
        prompt.text = ""
    }
}
