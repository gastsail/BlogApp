package com.example.blogapp.data.remote.home

import com.example.blogapp.core.Result
import com.example.blogapp.data.model.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class HomeScreenDataSource {

    suspend fun getLatestPosts(): Result<List<Post>> {
        val postList = mutableListOf<Post>()
        val querySnapshot = FirebaseFirestore.getInstance().collection("posts").get().await()
        for(post in querySnapshot.documents){
            post.toObject(Post::class.java)?.let { fbPost ->
                fbPost.apply { post_timestamp = post.getTimestamp("post_timestamp",DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate() }
                postList.add(fbPost)
            }
        }
        return Result.Success(postList)
    }
}