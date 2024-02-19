package com.example.foodbook

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class AIModel : ViewModel() {
    private val _textResult = MutableLiveData<String>()
    val textResult: LiveData<String> = _textResult

    val generativeModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = BuildConfig.apiKey
    )

    fun generateText(bitMap : Bitmap, string: String) {
        viewModelScope.launch {
            val inputImage: Bitmap = bitMap
            val inputContent = content {
                image(inputImage)
                text(string)
            }

            val response = generativeModel.generateContent(inputContent)
            setTextResult((response.text).toString())
        }
    }

    fun setTextResult(result: String) {
        _textResult.value = result
    }
}
