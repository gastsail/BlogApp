package com.example.blogapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.blogapp.core.Result
import com.example.blogapp.data.model.Post
import com.example.blogapp.domain.home.HomeScreenRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeScreenViewModel(private val repo: HomeScreenRepo): ViewModel() {

    fun fetchLatestPosts() = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(repo.getLatestPosts())
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }


    // with Flow coroutine builder
    val latestPosts: StateFlow<Result<List<Post>>> = flow {
        kotlin.runCatching {
            repo.getLatestPosts()
        }.onFailure { throwable ->
            emit(Result.Failure(Exception(throwable)))
        }.onSuccess { postList ->
            emit(postList)
        }
    }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000), // Or Lazily because it's a one-shot
            initialValue = Result.Loading()
    )

    //without Flow coroutine builder
    private val posts = MutableStateFlow<Result<List<Post>>>(Result.Loading())

    fun fetchPosts() {
        viewModelScope.launch {
            kotlin.runCatching {
                repo.getLatestPosts()
            }.onFailure { throwable ->
                posts.value = Result.Failure(Exception(throwable))
            }.onSuccess { postList ->
                posts.value = postList
            }
        }
    }

    fun getPosts() = posts

}


class HomeScreenViewModelFactory(private val repo: HomeScreenRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(HomeScreenRepo::class.java).newInstance(repo)
    }

}


