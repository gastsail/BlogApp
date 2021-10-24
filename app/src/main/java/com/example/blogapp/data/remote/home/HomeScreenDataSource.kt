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
    suspend fun getLatestPosts(): Result<List<Post>> {
        val postList = mutableListOf<Post>()

        withContext(Dispatchers.IO) {
            val querySnapshot = FirebaseFirestore.getInstance().collection("posts").get().await()
            for (post in querySnapshot.documents) {
                post.toObject(Post::class.java)?.let { fbPost ->

                    val isLiked = fbPost.poster?.uid?.let { safeUid ->
                        isPostLiked(post.id, safeUid)
                    }

                    fbPost.apply {
                        created_at = post.getTimestamp(
                            "created_at",
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )?.toDate()
                        id = post.id

                        if (isLiked != null) {
                            liked = isLiked
                        }
                    }
                    postList.add(fbPost)
                }
            }
        }

        return Result.Success(postList)
    }

    private suspend fun isPostLiked(postId: String, uid: String): Boolean {
        val post = FirebaseFirestore.getInstance().collection("postsLikes").document(postId).get().await()
        return post.contains(uid)
    }

     fun registerLikeButtonState(postId: String, uid: String, liked: Boolean): Result<Boolean> {

        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postId)
        val feelingsRef =  FirebaseFirestore.getInstance().collection("postsLikes").document(postId)

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