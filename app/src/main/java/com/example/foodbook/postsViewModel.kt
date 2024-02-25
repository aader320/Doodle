package com.example.foodbook

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


interface OnPostClickListener {
    fun onPostClick(post: Post)
}


class PostAdapter(private val context: Context, private val listener: OnPostClickListener)
    : ListAdapter<Post, PostAdapter.ViewHolder>(PostDiffCallback())
    //: RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val myPost = currentList[position]
        Glide.with(context)
            .load(myPost.imageUrl)
            .placeholder((R.drawable.logo))
            .error(R.drawable.ic_showpassword)
            .into(holder.imageView)

        holder.bind(myPost)
    }

    override fun getItemCount(): Int {
        //return Posts.size
        return currentList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPost)
        val captionText: TextView = itemView.findViewById(R.id.textViewCaption)
        val usernameText: TextView = itemView.findViewById(R.id.textViewUsername)
        //val locationText: TextView = itemView.findViewById(R.id.textViewLocation)
        val timeSinceEpochText: TextView = itemView.findViewById(R.id.textViewTime)
        val locationnameText: TextView = itemView.findViewById(R.id.textViewLocationName)
        val ratingbar: RatingBar = itemView.findViewById(R.id.ratingBar2)

        private fun convertEpochToDateTime(epochTime: Long): String
        {
            val date = Date(epochTime)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(date)
        }

        fun bind(post: Post)
        {
            captionText.text        = post.caption
            usernameText.text       = post.userEmail
            //locationText.text       = post.location
            locationnameText.text   = post.location_name
            timeSinceEpochText.text = convertEpochToDateTime(post.dateTime)
            ratingbar.rating        = post.price_range.toFloat()

            itemView.setOnClickListener {
                listener.onPostClick(post)
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.post_id == newItem.post_id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
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

    fun getAllPostsFlow(): Flow<List<Post>> {
        return repository.allPosts
    }

    fun clearAllPosts() {
        postsScope.async { repository.clearDatabase() }
    }
}



class commentsViewModel: ViewModel()
{
    private val commentsLiveData = MutableLiveData<List<Comment>>()

    // update the comments
    fun updateComments(comments: List<Comment>) {
        commentsLiveData.value = comments
    }

    fun addComment(comment: Comment) {
        val currentComments = commentsLiveData.value.orEmpty().toMutableList()
        currentComments.add(comment)
        commentsLiveData.value = currentComments
    }

    // function to observe the comments data from the viewmodel
    fun getCommentsLiveData()
        : LiveData<List<Comment>> {
        return commentsLiveData
    }

    fun getCommentLiveDataSize()
        : Int {
        return commentsLiveData.value?.size ?: 0
    }

    fun clearComments() {
        commentsLiveData.value = emptyList()
    }
}




class CommentsAdapter(private val context: Context)
    : ListAdapter<Comment, CommentsAdapter.commentsViewHolder>(CommentsAdapter.CommentsDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.commentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false)
        return commentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: commentsViewHolder, position: Int)
    {
        val myComment = getItem(position)
        holder.bind(myComment)
    }

    override fun getItemCount(): Int {
        //return Posts.size
        return currentList.size
    }

    inner class commentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val imageView: ImageView = itemView.findViewById(R.id.commentUserImage)
        val commentUsername: TextView = itemView.findViewById(R.id.commentUsername)
        val commentText: TextView = itemView.findViewById(R.id.commentTextview)

        private fun convertEpochToDateTime(epochTime: Long): String
        {
            val date = Date(epochTime)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(date)
        }

        fun bind(mycomment: Comment)
        {
            commentUsername.text = mycomment.userEmail + " <" + convertEpochToDateTime(mycomment.datetime.toLong()) + "> "
            commentText.text = mycomment.comment
        }
    }

    class CommentsDiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.comment_id == newItem.comment_id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}


































