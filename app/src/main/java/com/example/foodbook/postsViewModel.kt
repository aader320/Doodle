package com.example.foodbook

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.IllegalArgumentException

class PostAdapter(private val context: Context, private val Posts: List<Post>)
    : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
     val postView: Post? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val myPost = Posts[position]
        Glide.with(context)
            .load(myPost.imageUrl)
            .placeholder((R.drawable.logo))
            .error(R.drawable.ic_showpassword)
            .into(holder.imageView)

        holder.captionText.text = myPost.caption
        holder.usernameText.text = myPost.userEmail
        holder.timeSinceEpochText.text = myPost.dateTime.toString()
        holder.locationText.text = myPost.location
    }

    override fun getItemCount(): Int {
        return Posts.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPost)
        val captionText: TextView = itemView.findViewById(R.id.textViewCaption)
        val usernameText: TextView = itemView.findViewById(R.id.textViewUsername)
        val locationText: TextView = itemView.findViewById(R.id.textViewLocation)
        val timeSinceEpochText: TextView = itemView.findViewById(R.id.textViewTime)
    }
}

class PostViewModelFactory(val appl: Application): ViewModelProvider.Factory
{
    override fun <T:ViewModel> create(modelClass: Class<T>): T
    {
        if(modelClass.isAssignableFrom(postsViewModel::class.java)) {
            return postsViewModel(appl) as T
        }
        throw IllegalArgumentException("Unknown view model class initialization")
    }
}


class postsViewModel(appl: Application)
    : AndroidViewModel(appl)
{
    private var repository : PostsRepository? = null
    //val postsScope = CoroutineScope(Dispatchers.IO)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = PostDatabase.getInstance(appl).postDAO()
            repository = PostsRepository(dao)
        }
    }

    fun insert(post: Post) = viewModelScope.launch {
        repository?.insert(post)
    }

    fun getPostSize(): Int? {
        return repository?.allPosts?.size
    }

    fun getAllPosts(): List<Post>? {
        println("DEBUG>> getallpost() size:  ${getPostSize()}")
        return repository?.allPosts;
    }

    fun initializePostsRepository() {
        getAllFilesInMainFolder()
    }

    private fun getAllFilesInMainFolder()
    {
        val filepathString: String = "uploads"
        val folderRef: StorageReference = FirebaseStorage.getInstance().getReference(filepathString)

        CoroutineScope(Dispatchers.Main).launch {
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
                        //posts.add(Post(imageUrl = imageDownloadURL, caption = imageCaption, location = imageLocation, userEmail = imageUserEmail, dateTime = imageTimePosted.toLong())) // Increment completed tasks
                        insert(Post(imageUrl = imageDownloadURL, caption = imageCaption, location = imageLocation, userEmail = imageUserEmail, dateTime = imageTimePosted.toLong()))
                    }
                }

                println(">> TEST USERPOST SIZE: ${getPostSize()}")
            }
            catch (e: Exception) {
                // Handle exceptions
                println("Failed to list files in folder: ${folderRef.path}, Error: ${e.message}")
            }
        }
    }
}







































