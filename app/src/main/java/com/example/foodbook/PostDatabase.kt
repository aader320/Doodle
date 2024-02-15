package com.example.foodbook

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Entity(tableName = "Posts_Table")
data class Post (
    @PrimaryKey(autoGenerate = true)
    val post_id: Int = 0,

    @ColumnInfo(name = "imageUrl")
    val imageUrl: String,

    @ColumnInfo(name = "caption")
    val caption: String,

    // add money

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "userEmail")
    val userEmail: String,

    @ColumnInfo(name = "dateTime")
    val dateTime: Long
)


@Dao
interface PostsDAO
{
    @Insert
    suspend fun insertPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Query("SELECT * FROM Posts_Table ORDER BY post_id ASC")
    fun getAllPosts(): List<Post>

    @Query("SELECT * FROM Posts_Table WHERE dateTime = :postDateTime ORDER BY post_id ASC")
    fun getPostsByDateTime(postDateTime: Long): List<Post>

    @Query("SELECT * FROM Posts_Table WHERE userEmail = :inputUserEmail ORDER BY post_id ASC")
    fun getPostsByUser(inputUserEmail : String): List<Post>
}


@Database(entities = [Post::class], version = 1)
abstract class PostDatabase : RoomDatabase()
{
    abstract fun postDAO(): PostsDAO

    companion object
    {
        private const val DATABASE_NAME = "PostsDatabase"

        @Volatile
        private var INSTANCE: PostDatabase? = null

        fun getInstance(context: Context): PostDatabase
        {
            println("I AM INSIDE THE GETINSTANCE OF MY POST DATABASEEEEE")
            return INSTANCE ?: synchronized(this)
            {
                println("I AM CREATING A NEW INSTANCE OF MY POST DATABASEEEEE")

                try {
                    val instance = Room.databaseBuilder(
                        context,
                        PostDatabase::class.java,
                        DATABASE_NAME
                    ).build()

                    INSTANCE = instance
                    instance
                }
                catch (e: Exception) {
                    println("ERROR: ${e.message}")
                    throw e
                }
            }
        }
    }
}


class PostsRepository(private val postsDAO: PostsDAO)
{
    public var allPosts: List<Post> = postsDAO.getAllPosts()

    suspend fun insert(post: Post) {
        postsDAO.insertPost(post)
    }

    suspend fun update(post: Post) {
        postsDAO.updatePost(post)
    }

    fun getPostsByDateTime(dateTime: Long): List<Post> {
        return postsDAO.getPostsByDateTime(dateTime)
    }

    fun getPostsByUser(userEmail: String): List<Post> {
        return postsDAO.getPostsByUser(userEmail)
    }
}