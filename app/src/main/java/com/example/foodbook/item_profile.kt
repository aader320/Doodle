package com.example.foodbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment

class item_profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_profile)

        val buttonOpenFragment = findViewById<Button>(R.id.loc)
        buttonOpenFragment.setOnClickListener {
            // Replace 'YourFragment' with the fragment you want to open
            replaceFragment(MapsFragment())

        }

    }

        private fun replaceFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_map, fragment)
                .addToBackStack(null)
                .commit()
        }
}