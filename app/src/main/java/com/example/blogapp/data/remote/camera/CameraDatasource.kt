package com.example.blogapp.data.remote.camera

import android.graphics.Bitmap
import com.example.blogapp.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class CameraDatasource {

    suspend fun uploadPhoto(imageBitmap: Bitmap, description: String): Boolean {
        val imageRef = FirebaseStorage.getInstance().reference.child("images/lala")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val downloadUrl =  imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()
        FirebaseAuth.getInstance().uid?.let { FirebaseFirestore.getInstance().collection("posts").add(Post(post_image = downloadUrl, post_description = description)) }
        return true
    }
}