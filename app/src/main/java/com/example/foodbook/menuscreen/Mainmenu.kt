package com.example.foodbook.menuscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodbook.databinding.ActivityMainBinding

class Mainmenu : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}