package com.example.blogapp.data.remote.home

import com.example.blogapp.core.Result
import com.example.blogapp.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class HomeScreenDataSource {

    @ExperimentalCoroutinesApi
    suspend fun getLatestPosts(): Flow<Result<List<Post>>> = callbackFlow {
        val postList = mutableListOf<Post>()

        var postReference: Query? = null

        try {
            postReference = FirebaseFirestore.getInstance().collection("posts")
                .orderBy("created_at", Query.Direction.DESCENDING)
        } catch (e: Throwable) {
            close(e)
        }

        val suscription = postReference?.addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            try {
                postList.clear()
                for (post in value.documents) {
                    post.toObject(Post::class.java)?.let { fbPost ->
                        fbPost.apply {
                            created_at = post.getTimestamp(
                                "created_at",
                                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                            )?.toDate()
                            likes = post.getLong("likes") ?: 0
                            id = post.id
                            //TODO CHECK THIS LOGIC
                            launch {
                                liked = isPostLiked(id, FirebaseAuth.getInstance().currentUser!!.uid)
                                postList.add(fbPost)
                                offer(Result.Success(postList))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                close(e)
            }
        }

        awaitClose { suscription?.remove() }
    }

    private suspend fun isPostLiked(postId: String, uid: String): Boolean {
        val post = FirebaseFirestore.getInstance().collection("feelings").document(postId).get().await()
        return post.contains(uid)
    }

     fun registerLikeButtonState(postId: String, uid: String, liked: Boolean): Result<Boolean> {

        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postId)
        val feelingsRef =  FirebaseFirestore.getInstance().collection("feelings").document(postId)

        val batch = FirebaseFirestore.getInstance().batch()

        if(liked) {
            batch.set(postRef, hashMapOf("likes" to increment), SetOptions.merge())
            batch.set(feelingsRef, hashMapOf(uid to true), SetOptions.merge())
        } else {
            batch.set(postRef, hashMapOf("likes" to decrement), SetOptions.merge())
            batch.delete(feelingsRef)
        }

        batch.commit()

        return if(liked) {
            Result.Success(true)
        } else {
            Result.Success(false)
        }
    }
}