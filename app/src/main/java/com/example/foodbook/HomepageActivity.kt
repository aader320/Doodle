package com.example.foodbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodbook.R
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.common.primitives.UnsignedLong
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomepageActivity : AppCompatActivity() {
    lateinit var itemPostRecyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        itemPostRecyclerview = findViewById<RecyclerView>(R.id.itemPostRecyclerview)
        val layoutManager = LinearLayoutManager(this)
        itemPostRecyclerview.layoutManager = layoutManager

        val filepathString: String = "uploads"
        val lStorage: StorageReference = FirebaseStorage.getInstance().getReference(filepathString)

        getAllFilesInMainFolder(lStorage)
    }


    // Inside your function
    private fun getAllFilesInMainFolder(folderRef: StorageReference)
    {
        CoroutineScope(Dispatchers.Main).launch {
            val posts = mutableListOf<Post>()

            try
            {
                // Await the entire list operation
                val listResult = folderRef.listAll().await()

                val subfolderTasks = mutableListOf<Task<ListResult>>()

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
                        var imageUserEmail = metadata.getCustomMetadata("User_Email") ?: "No user email"
                        var imageTimePosted = metadata.getCustomMetadata("TimeSinceEpoch") ?: "1000"

                        // Add the post to the list
                        posts.add(Post(imageUrl = imageDownloadURL, caption = imageCaption, location = imageLocation, userEmail = imageUserEmail, dateTime = imageTimePosted.toULong())) // Increment completed tasks
                    }
                }

                // Update UI after all tasks are completed
                val adapter = PostAdapter(this@HomepageActivity, posts)
                itemPostRecyclerview.adapter = adapter
                println(">> TEST USERPOST SIZE: ${posts.size}")
            } catch (e: Exception) {
                // Handle exceptions
                println("Failed to list files in folder: ${folderRef.path}, Error: ${e.message}")
            }
        }
    }
}



//val fileref = lStorage.child("abc@gmail.com/1706760956196.jpeg")
//        fileref.downloadUrl
//            .addOnSuccessListener { downloadURL->
//                //println(">> TEST: ${downloadURL}")
//            }