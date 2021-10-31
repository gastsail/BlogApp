package com.example.blogapp.data.remote.camera

import android.graphics.Bitmap
import com.example.blogapp.data.model.Post
import com.example.blogapp.data.model.Poster
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class CameraDataSource {

    suspend fun uploadPhoto(imageBitmap: Bitmap, description: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val randomName = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().reference.child("${user?.uid}/posts/$randomName")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var downloadUrl = ""
        withContext(Dispatchers.IO) {
            downloadUrl = imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()
        }
        user?.let {
            it.displayName?.let { displayName ->
                FirebaseFirestore.getInstance().collection("posts").add(Post(
                        likes = 0,
                        profile_name = displayName,
                        profile_picture = it.photoUrl.toString(),
                        post_image = downloadUrl,
                        post_description = description,
                        poster = Poster(username = user.displayName, uid = user.uid)))
            }
        }

    }
}