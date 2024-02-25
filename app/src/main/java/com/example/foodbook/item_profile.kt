package com.example.foodbook

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodbook.Activities.userEmail
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storageMetadata
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


lateinit var commentEditText: EditText
lateinit var commentViewModel: commentsViewModel

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
        try {
            commentViewModel = ViewModelProvider(requireActivity()).get(commentsViewModel::class.java)
        } catch (e: Exception) {
            println("ERROR INITIALIZING COMMENT VIEW MODEL: ${e.message}")
        }

        val view: View = inflater.inflate(R.layout.activity_item_page, container, false)
        val buttonopen = view.findViewById<Button>(R.id.loc)
        val itemimage = view.findViewById<ImageView>(R.id.ItemImage)
        val titleTextView = view.findViewById<TextView>(R.id.Title)
        val postAuthor = view.findViewById<TextView>(R.id.posterer)
        val descriptionText = view.findViewById<TextView>(R.id.tag_state_description)
        val ratingbar = view.findViewById<RatingBar>(R.id.ratingBar)
        val sendCommentButton = view.findViewById<Button>(R.id.ButtonSendComment)
        commentEditText = view.findViewById(R.id.CommentsEditText)
        val commentsHeadercount = view.findViewById<TextView>(R.id.CommentsHeaderTextView)
        val commentsRecyclerView = view.findViewById<RecyclerView>(R.id.commentsRecyclerView)
        val layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.layoutManager = layoutManager

        postAuthor.text = "By: " + mypost.userEmail.substringBefore('@') + " (" + convertEpochToDateTime(mypost.dateTime) + ")"
        descriptionText.text = mypost.caption
        ratingbar.rating = mypost.price_range.toFloat()

        // Comments
        val post_filepath: String = "comments/" + mypost.dateTime
        val storageRef: StorageReference = FirebaseStorage.getInstance().getReference(post_filepath)
        GetAllCommentsFromPost(storageRef)

//        commentViewModel.addComment(Comment(comment = "tetestes", userEmail = "abc", datetime = mypost.dateTime.toString()))
//        commentViewModel.addComment(Comment(comment = "tetestes", userEmail = "abc", datetime = mypost.dateTime.toString()))
//        commentViewModel.addComment(Comment(comment = "tetestes", userEmail = "abc", datetime = mypost.dateTime.toString()))
//        commentViewModel.addComment(Comment(comment = "tetestes", userEmail = "abc", datetime = mypost.dateTime.toString()))
//        commentViewModel.addComment(Comment(comment = "tetestes", userEmail = "abc", datetime = mypost.dateTime.toString()))

        val commentsadapter = CommentsAdapter(requireContext())
        commentsRecyclerView.adapter = commentsadapter
        commentsHeadercount.text = "Comments (" + commentViewModel.getCommentLiveDataSize() + ")"

        commentViewModel.getCommentsLiveData().observe(viewLifecycleOwner, Observer { comments ->
            commentsadapter.submitList(comments)
            commentsadapter.notifyDataSetChanged()
            commentsHeadercount.text = "Comments (" + commentViewModel.getCommentLiveDataSize() + ")"

            println("Comment Obsever: ${comments.toString()}")
        })

        Glide.with(this)
            .load(mypost.imageUrl)
            .placeholder((R.drawable.logo))
            .error(R.drawable.ic_showpassword)
            .into(itemimage)

        println(">> location name: ${mypost.location_name}")
        titleTextView.text = mypost.location_name

        sendCommentButton.setOnClickListener() {
            commentViewModel.addComment(Comment(comment = commentEditText.text.toString(), userEmail = userEmail, datetime = System.currentTimeMillis().toString()))
            uploadComment(mypost.dateTime.toString())   // upload a comment regarding this specific post
        }

        buttonopen.setOnClickListener {
//            openFragment() // need to uncomment it          ZX
            openGoogleMaps(mypost.location)
        }

        Glide.with(this)
            .asBitmap()
            .load(mypost.imageUrl)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    BitmapHolder.bitmap = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    BitmapHolder.bitmap = null
                }
            })

        view.findViewById<Button>(R.id.AI).setOnClickListener {
            val activity = requireActivity()
            val intent = Intent(activity, AI::class.java)
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


private fun uploadComment(post_timesinceepoch: String)
{
    val filepathString: String = "comments/" + post_timesinceepoch
    println("FilepathString: ${filepathString}")
    val lStorage: StorageReference = FirebaseStorage.getInstance().reference.child(filepathString)
    UploadFile(lStorage)
}

private fun UploadFile(Storage: StorageReference)
{
    val comment: String = commentEditText.text.toString()

    if(comment.isEmpty()) {
        return
    }
    commentEditText.text.clear()

    val comment_datetime = System.currentTimeMillis().toString()
    val postData = comment
    val metaData = storageMetadata {
        contentType = "txt"
        setCustomMetadata("Comment_DateTime", comment_datetime)
        setCustomMetadata("Comment", comment)
        setCustomMetadata("Commentor_UserEmail", userEmail)
    }
    println("user email: ${userEmail.toString()}")
    val postRef = Storage.child(comment_datetime)
    postRef.putBytes(postData.toByteArray(), metaData)
        .addOnSuccessListener {
            println("Comment ${comment} has been posted")
        }
        .addOnFailureListener() {e ->
            println("Comment failed to post >> ${e.message}")
        }
}

private fun GetAllCommentsFromPost(storage: StorageReference)
{
    println(">>> getallcommentsfrompost!!")
    commentViewModel.clearComments()

    CoroutineScope(Dispatchers.Main).launch {
        try
        {
            val subfolder = storage.listAll().await()
            subfolder.items.forEach { item->
                val itemMetadata = item.metadata.await()
                val commentDateTime: String = itemMetadata.getCustomMetadata("Comment_DateTime").toString()
                val comment: String         = itemMetadata.getCustomMetadata("Comment").toString()
                val commentorUserEmail: String = itemMetadata.getCustomMetadata("Commentor_UserEmail").toString()

                println("useremail ${commentorUserEmail}, comment_datetime ${commentDateTime}, comment: ${comment}")
                commentViewModel.addComment(
                    Comment(
                        comment = comment,
                        userEmail = commentorUserEmail,
                        datetime = commentDateTime
                    )
                )
            }

            println("viewmodel comments size: ${commentViewModel.getCommentLiveDataSize()}")
        } catch (e: Exception) {
            println(">> ERROR: Failed to retrieve comments. Error: ${e.message}")
        }
    }
}