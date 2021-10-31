package com.example.blogapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.blogapp.R
import com.example.blogapp.core.Result
import com.example.blogapp.core.hide
import com.example.blogapp.core.show
import com.example.blogapp.data.model.Post
import com.example.blogapp.data.remote.home.HomeScreenDataSource
import com.example.blogapp.databinding.FragmentHomeScreenBinding
import com.example.blogapp.domain.home.HomeScreenRepoImpl
import com.example.blogapp.presentation.home.HomeScreenViewModel
import com.example.blogapp.presentation.home.HomeScreenViewModelFactory
import com.example.blogapp.ui.home.adapter.HomeScreenAdapter
import com.example.blogapp.ui.home.adapter.OnPostClickListener

class HomeScreenFragment : Fragment(R.layout.fragment_home_screen), OnPostClickListener {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel> {
        HomeScreenViewModelFactory(
                HomeScreenRepoImpl(
                        HomeScreenDataSource()
                )
        )
    }

    private val adapter = HomeScreenAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)
        binding.rvHome.adapter = adapter

        viewModel.fetchLatestPosts().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }

                is Result.Success -> {
                    binding.progressBar.hide()
                    if(result.data.isEmpty()) {
                        binding.emptyContainer.show()
                        return@Observer
                    }else{
                        binding.emptyContainer.hide()
                    }
                    adapter.setPostData(result.data)
                }

                is Result.Failure -> {
                    binding.progressBar.hide()
                    Toast.makeText(
                            requireContext(),
                            "Ocurrio un error: ${result.exception}",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override fun onLikeButtonClick(post: Post, liked: Boolean) {
            viewModel.registerLikeButtonState(post.id, liked).observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Loading -> {
                        Log.d("Like Transaction", "in progress ....")
                    }

                    is Result.Success -> {
                        Log.d("Like Transaction", "finished")
                    }

                    is Result.Failure -> {
                        // Here we should unmark the like since the transaction failed from the server
                        Toast.makeText(
                            requireContext(),
                            "Error liking or disliking post: ${result.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }