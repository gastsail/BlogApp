package com.example.blogapp.data.remote.home

import com.example.blogapp.core.Result
import com.example.blogapp.data.model.Post
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class HomeScreenDataSource(private val firestore: FirebaseFirestore) {

    @ExperimentalCoroutinesApi
    suspend fun getLatestPosts(): Flow<Result<List<Post>>> = callbackFlow {

        val postList = mutableListOf<Post>()

        // Reference to use in Firestore
        var eventsCollection: Query? = null
        try {
            eventsCollection = firestore.collection("posts").orderBy("created_at", Query.Direction.DESCENDING)
        } catch (e: Throwable) {
            // If Firebase cannot be initialized, close the stream of data
            // flow consumers will stop collecting and the coroutine will resume
            close(e)
        }

        val suscription = eventsCollection?.addSnapshotListener { value, error ->
            if (value == null) {
                return@addSnapshotListener
            }

            try {
                postList.clear()
                for (post in value.documents) {
                    post.toObject(Post::class.java)?.let { fbPost ->
                        fbPost.apply {
                            created_at = post.getTimestamp(
                                "created_at",
                                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                            )?.toDate()
                        }
                        postList.add(fbPost)
                    }
                }
                offer(Result.Success(postList))
            } catch (e: Exception) {
                close(e)
            }
        }

        awaitClose { suscription?.remove() }
    }
}