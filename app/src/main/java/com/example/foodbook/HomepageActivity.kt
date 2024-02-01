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
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

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

        val fileref = lStorage.child("abc@gmail.com/1706760956196.jpeg")
        fileref.downloadUrl
            .addOnSuccessListener { downloadURL->
                //println(">> TEST: ${downloadURL}")
            }

        listFilesInFolder(lStorage)
    }

    private fun listFilesInFolder(folderRef: StorageReference)
    {
        val posts = mutableListOf<Post>()
        var completed_tasks: Int = 0
        var totalTasks: Int = 0

        folderRef.listAll()
            .addOnSuccessListener { listResult ->
                val subfolderTasks = mutableListOf<Task<ListResult>>()
                totalTasks = listResult.prefixes.size

                // Iterate through each subfolder
                listResult.prefixes.forEach { subfolder ->
                    // List all items in the subfolder
                    println("Iterating thorugh subfolder: ${subfolder.path}")
                    val task = subfolder.listAll()

                        // this is for each subfolder (users)
                        .addOnSuccessListener { subfolderListResult ->

                            // this is for each item within the subfolder (images)
                            subfolderListResult.items.forEach { item ->
                                var imageDownloadURL: String = "default"
                                var imageCaption: String = "default"
                                var imageLocation: String = "default"
                                val imagepath: String = item.path.removePrefix("/uploads/")
                                val lfileRef = folderRef.child(imagepath)
                                println("imagepath: ${imagepath}")

                                // this is the checked for getting the download url
                                lfileRef.downloadUrl
                                    .addOnSuccessListener {downloadURL->
                                        imageDownloadURL = downloadURL.toString()
                                        println("FINAL DOWNLOAD STRING: ${downloadURL.toString()}")
                                    }
                                    .addOnFailureListener { exception ->
                                        // Handle failure to download URL
                                        println("Failed to download URL: ${exception.message}")
                                    }

                                // this is to get the image's metadata
                                lfileRef.metadata
                                    .addOnSuccessListener { imagemetadata->
                                        imageCaption = imagemetadata.getCustomMetadata("Caption").toString()
                                        imageLocation = imagemetadata.getCustomMetadata("Location").toString()

                                        posts.add(Post(imageUrl = imageDownloadURL, caption = imageCaption, location = imageLocation))
                                        // check if all the tasks are completed
                                        println("completed: ${completed_tasks} / ${totalTasks}")
                                        if(++completed_tasks == totalTasks)
                                        {
                                            val adapter = PostAdapter(this, posts)
                                            itemPostRecyclerview.adapter = adapter
                                            println(">> TEST USERPOST SIZE: ${posts.size}")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        // Handle failure to download URL
                                        println("Failed to download metadata: ${exception.message}")
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure to list items in the subfolder
                            println("Failed to list items in subfolder: ${subfolder.path}, Error: ${exception.message}")
                        }

                    subfolderTasks.add(task)
                }

                // Wait for all subfolder tasks to complete
                Tasks.whenAllComplete(subfolderTasks)
                    .addOnSuccessListener {
                        // All tasks completed successfully, invoke the callback with the posts list
//                        val adapter = PostAdapter(this, posts)
//                        itemPostRecyclerview.adapter = adapter
//                        println(">> TEST USERPOST SIZE: ${posts.size}")
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure of waiting for subfolder tasks
                        println("Failed to wait for subfolder tasks: ${exception.message}")
                        // Invoke the callback with an empty list
                        //callback(emptyList())
                    }
            }
            .addOnFailureListener { exception ->
                // Handle failure to list items in the main folder
                println("Failed to list items in main folder: ${exception.message}")
                // Invoke the callback with an empty list
                //callback(emptyList())
            }
    }



    // List all items (files and subfolders) in the folder
//        folderRef.listAll()
//            .addOnSuccessListener { listResult ->
//                // Loop through each item in the folder
//                listResult.items.forEach { item ->
//                    // Get the metadata of the item
//                    item.metadata
//                        .addOnSuccessListener { metadata ->
//                            // Check if the item is a file based on its metadata
//                            val isFile = metadata.contentType != null
//                            if (isFile) {
//                                // It's a file, handle accordingly
//                                val fileName = item.name
//                                val fileCaption = metadata.getCustomMetadata("Caption")
//                                val fileLocation = metadata.getCustomMetadata("Location")
//                                val downloadUrl = item.downloadUrl.toString()
//
//                                // Log the details of the file
//                                Log.d("File Details", "Name: $fileName, Caption: $fileCaption, Location: $fileLocation, Download URL: $downloadUrl")
//
//                                // Add the file details to the list of posts
//                                posts.add(Post(caption = fileCaption!!, location = fileLocation!!, imageUrl = downloadUrl))
//                            } else {
//                                // It's a folder, recursively list its contents
//                                listFilesInFolder(item)
//                            }
//                        }
//                        .addOnFailureListener { exception ->
//                            Log.e("Metadata Retrieval", "Failed to retrieve metadata: ${exception.message}")
//                        }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("List Items", "Failed to list items: ${exception.message}")
//            }
}