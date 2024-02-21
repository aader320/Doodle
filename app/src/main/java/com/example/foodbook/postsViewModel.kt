package com.example.foodbook

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch



interface OnPostClickListener {
    fun onPostClick(post: Post)
}



class PostAdapter(private val context: Context, private val Posts: List<Post>, private val listener: OnPostClickListener)
    : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
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

        holder.bind(myPost)
    }

    override fun getItemCount(): Int {
        return Posts.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPost)
        val captionText: TextView = itemView.findViewById(R.id.textViewCaption)
        val usernameText: TextView = itemView.findViewById(R.id.textViewUsername)
        val locationText: TextView = itemView.findViewById(R.id.textViewLocation)
        val timeSinceEpochText: TextView = itemView.findViewById(R.id.textViewTime)

        fun bind(post: Post)
        {
            captionText.text        = post.caption
            usernameText.text       = post.userEmail
            timeSinceEpochText.text = post.dateTime.toString()
            locationText.text       = post.location

            itemView.setOnClickListener {
                listener.onPostClick(post)
            }
        }
    }
}



class postsViewModel(appl: Application)
    : AndroidViewModel(appl)
{
    private var repository: PostsRepository
    val postsScope = CoroutineScope(Dispatchers.IO)

    init {
        val dao = PostDatabase.getInstance(appl).postDAO()
        repository = PostsRepository(dao)
    }

    fun insert(post: Post) = viewModelScope.launch {
        repository.insert(post)
    }

    fun getPostSize(): Deferred<Int> {
        return postsScope.async{repository.allPosts.first().size}
    }

    fun getAllPosts(): Deferred<List<Post>> {
        // call .await() when calling this function
        return postsScope.async { repository.allPosts.first() }
    }
}







































