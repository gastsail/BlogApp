package com.example.blogapp.data.remote.auth

import android.graphics.Bitmap
import android.net.Uri
import com.example.blogapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class AuthDataSource {

    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return withContext(Dispatchers.IO) {
            val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            authResult.user
        }
    }

    suspend fun signUp(email: String, password: String, username: String): FirebaseUser? {
        return withContext(Dispatchers.IO) {
            val authResult =
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            authResult.user?.uid?.let { uid ->
                FirebaseFirestore.getInstance().collection("users").document(uid).set(
                    User(
                        email,
                        username
                    )
                )
            }
            authResult.user
        }
    }

    suspend fun updateUserProfile(imageBitmap: Bitmap, username: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val imageRef = FirebaseStorage.getInstance().reference.child("${user?.uid}/profile_picture")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var downloadUrl = ""
        withContext(Dispatchers.IO) {
            downloadUrl = imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(Uri.parse(downloadUrl))
                .build()
            user?.updateProfile(profileUpdates)?.await()
        }
    }
}