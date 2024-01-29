package com.example.foodbook

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.Toast
import com.example.foodbook.databinding.ActivityUploadBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private var mName: String? = null
private var mImageURL: String? = null
private var mImageURI: Uri? = null

public lateinit var mStorageRef: StorageReference
public lateinit var mDatabaseRef: DatabaseReference

class UploadLogic {
    // empty constructor
    constructor (name: String, imageURL: String)
    {
        var mutablename = name
        if(mutablename.trim() == "") {
            mutablename = "Default Caption"
        }

        mName = mutablename
        mImageURL = imageURL
    }
}

public fun initUploadVariables()
{
    mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
    mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads")
}

public fun getName(): String? { return mName }
public fun getImageURL(): String? { return mImageURL }
public fun setName(name: String) { mName = name }
public fun setImageURL(imageURL: String) { mImageURL = imageURL }
public fun setImageURI(imageURI: Uri) { mImageURI = imageURI }
public fun getImageURI() :Uri? { return mImageURI }

public fun getFileExtension(context: Context, myUri: Uri): String {
    val cr: ContentResolver = context.contentResolver
    val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(cr.getType(myUri)) ?: ""
}







