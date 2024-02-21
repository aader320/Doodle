package com.example.foodbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class item_profile : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
//            return inflater.inflate(R.layout.activity_item_profile, container, false)

            val view: View = inflater.inflate(R.layout.activity_item_profile, container, false)

            val buttonopen = view.findViewById<Button>(R.id.loc)
            buttonopen.setOnClickListener {
//            openFragment()
            }
            return view
        }

//    private fun openFragment() {
//        val fragmentB = MapsFragment()
//        val transaction = childFragmentManager.beginTransaction()
//        transaction.replace(R.id.fragment_map, fragmentB)
//        transaction.addToBackStack(null) // Optional: Add to back stack if needed
//        transaction.commit()
//    }

}
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_item_profile)
//
//        val buttonOpenFragment = findViewById<Button>(R.id.loc)
//        buttonOpenFragment.setOnClickListener {
//            // Replace 'YourFragment' with the fragment you want to open
//            replaceFragment(MapsFragment())
//
//        }
//
//    }

//        private fun replaceFragment(fragment: Fragment) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_map, fragment)
//                .addToBackStack(null)
//                .commit()
//        }
//}