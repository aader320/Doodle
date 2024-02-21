package com.example.foodbook.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import com.example.foodbook.OnPostClickListener
import com.example.foodbook.Post
import com.example.foodbook.PostAdapter
import com.example.foodbook.R
import com.example.foodbook.postsViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomepageActivity : AppCompatActivity(), OnPostClickListener
{
    lateinit var itemPostRecyclerview: RecyclerView
    private lateinit var postViewModel: postsViewModel

    override fun onPostClick(post: Post)
    {
        // this function handles each post click within the recyclerview HAN
        println("${post.caption} is clicked")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Initialize the viewmodel
        try {
            postViewModel = ViewModelProvider(this).get(postsViewModel::class.java)
        } catch (e: Exception) {
            println("ViewModelInit. >>> Error initializing ViewModel: ${e.message}")
        }

        itemPostRecyclerview = findViewById<RecyclerView>(R.id.itemPostRecyclerview)
        val layoutManager = LinearLayoutManager(this)
        itemPostRecyclerview.layoutManager = layoutManager

        val filepathString: String = "uploads"
        val lStorage: StorageReference = FirebaseStorage.getInstance().getReference(filepathString)

        getAllFilesInMainFolder(lStorage)
    }


    private fun getAllFilesInMainFolder(folderRef: StorageReference)
    {
        postViewModel.clearAllPosts()

        CoroutineScope(Dispatchers.Main).launch {
            val posts = mutableListOf<Post>()

            try
            {
                // Await the entire list operation
                val listResult = folderRef.listAll().await()

                // Iterate through each subfolder
                listResult.prefixes.forEach { subfolder ->
                    // List all items in the subfolder
                    println("Iterating through subfolder: ${subfolder.path}")
                    val subfolderListResult = subfolder.listAll().await()

                    // Process each item in the subfolder concurrently
                    subfolderListResult.items.forEach { item ->
                        // Define variables to store data
                        val imagePath = item.path.removePrefix("/uploads/")
                        val fileRef = folderRef.child(imagePath)
                        println("Image path: $imagePath")

                        // Execute downloadUrl and metadata tasks concurrently
                        val downloadUrlTask = fileRef.downloadUrl.await()
                        val metadata = fileRef.metadata.await()

                        // Process the results
                        var imageDownloadURL = downloadUrlTask.toString()
                        var imageCaption = metadata.getCustomMetadata("Caption") ?: "No caption"
                        var imageLocation = metadata.getCustomMetadata("Location") ?: "No location"
                        var imageLocationName = metadata.getCustomMetadata("Location_Name") ?: "No location"
                        var imageUserEmail = metadata.getCustomMetadata("User_Email") ?: "No user email"
                        var imageTimePosted = metadata.getCustomMetadata("TimeSinceEpoch") ?: "1000"
                        var imagePriceRange = metadata.getCustomMetadata("Price_Range") ?: "1"

                        // Add the post to the list
                        //posts.add(Post(imageUrl = imageDownloadURL, caption = imageCaption, location = imageLocation, userEmail = imageUserEmail, dateTime = imageTimePosted.toLong())) // Increment completed tasks
                        postViewModel.insert(Post(imageUrl = imageDownloadURL, caption = imageCaption, location = imageLocation, userEmail = imageUserEmail, dateTime = imageTimePosted.toLong(), location_name = imageLocationName, price_range = imagePriceRange))
                    }
                }

                // Update UI after all tasks are completed
                val adapter = PostAdapter(this@HomepageActivity, postViewModel.getAllPosts().await(), this@HomepageActivity)
                itemPostRecyclerview.adapter = adapter
                println(">> TEST USERPOST SIZE: ${posts.size}")
                println(">> TEST VIEWMODEL FLOW SIZE: ${postViewModel.getPostSize().await()}")
            }
            catch (e: Exception) {
                // Handle exceptions
                println("Failed to list files in folder: ${folderRef.path}, Error: ${e.message}")
            }
        }
    }
}