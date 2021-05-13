package com.example.blogapp.domain.home

import android.graphics.Bitmap
import com.example.blogapp.core.Result
import com.example.blogapp.data.model.Post
import com.example.blogapp.data.remote.home.HomeScreenDataSource

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource): HomeScreenRepo {
    override suspend fun getLatestPosts(): Result<List<Post>> = dataSource.getLatestPosts()
}