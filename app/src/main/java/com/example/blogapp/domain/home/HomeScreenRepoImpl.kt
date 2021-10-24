package com.example.blogapp.domain.home

import com.example.blogapp.core.Result
import com.example.blogapp.data.model.Post
import com.example.blogapp.data.remote.home.HomeScreenDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource) : HomeScreenRepo {

    @ExperimentalCoroutinesApi
    override suspend fun getLatestPosts(): Result<List<Post>> = dataSource.getLatestPosts()

    override suspend fun registerLikeButtonState(postId: String, uid: String, liked: Boolean): Result<Boolean> =
        dataSource.registerLikeButtonState(postId, uid, liked)
}