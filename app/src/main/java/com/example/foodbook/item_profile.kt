package com.example.foodbook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun convertEpochToDateTime(epochTime: Long): String
{
    val date = Date(epochTime)
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(date)
}

class item_profile(private val mypost: Post) : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        : View?
    {
//            return inflater.inflate(R.layout.activity_item_profile, container, false)

        val view: View = inflater.inflate(R.layout.activity_item_page, container, false)
        val buttonopen = view.findViewById<Button>(R.id.loc)
        val itemimage = view.findViewById<ImageView>(R.id.ItemImage)
        val titleTextView = view.findViewById<TextView>(R.id.Title)
        val postAuthor = view.findViewById<TextView>(R.id.posterer)
        val descriptionText = view.findViewById<TextView>(R.id.tag_state_description)
        val ratingbar = view.findViewById<RatingBar>(R.id.ratingBar)

        postAuthor.text = "By: " + mypost.userEmail.substringBefore('@') + " (" + convertEpochToDateTime(mypost.dateTime) + ")"
        descriptionText.text = mypost.caption
        ratingbar.rating = mypost.price_range.toFloat()

        Glide.with(this)
            .load(mypost.imageUrl)
            .placeholder((R.drawable.logo))
            .error(R.drawable.ic_showpassword)
            .into(itemimage)

        println(">> location name: ${mypost.location_name}")
        titleTextView.text = mypost.location_name

        buttonopen.setOnClickListener {
//            openFragment() // need to uncomment it          ZX
            openGoogleMaps(mypost.location)
        }

        view.findViewById<Button>(R.id.AI).setOnClickListener {
            val activity = requireActivity()
            val intent = Intent(activity, AI::class.java)
            intent.putExtra("bitmapUri", mypost.imageUrl)
            startActivity(intent)
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
    private fun openGoogleMaps(latlng: String)
    {
        val gmmIntentUri: Uri = Uri.parse("geo:0,0?q=${latlng}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // If Google Maps app is not installed, open in a web browser
            val webMapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=your+location"))
            startActivity(webMapIntent)
        }
    }

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