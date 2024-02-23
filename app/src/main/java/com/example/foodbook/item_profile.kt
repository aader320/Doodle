package com.example.foodbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.SupportMapFragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide

class item_profile(private val mypost: Post) : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        : View?
    {
//            return inflater.inflate(R.layout.activity_item_profile, container, false)

        val view: View = inflater.inflate(R.layout.activity_item_profile, container, false)
        val buttonopen = view.findViewById<Button>(R.id.loc)
        val itemimage = view.findViewById<ImageView>(R.id.ItemImage)
        val titleTextView = view.findViewById<TextView>(R.id.Title)

//        Glide.with(this)
//            .load(mypost.imageUrl)
//            .placeholder((R.drawable.logo))
//            .error(R.drawable.ic_showpassword)
//            .into(itemimage)

        println(">> location name: ${mypost.location_name}")
        titleTextView.text = mypost.location_name

        buttonopen.setOnClickListener {
//            openFragment() // need to uncomment it          ZX
        }
        return view
    }

    //ZX
//==========================================================================================
    //private fun openFragment()
    //{
    //    val fragmentB = fragmentManager?.findFragmentById(R.id.fragment_map)
    //    val transaction = childFragmentManager.beginTransaction()
    //    if (fragmentB != null) {
    //        transaction.replace(R.id.fragment_map, fragmentB)
    //    }
    //    transaction.addToBackStack(null) // Optional: Add to back stack if needed
    //    transaction.commit()
    //}
//==========================================================================================
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