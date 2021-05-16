package com.example.blogapp.data.model

import com.google.firebase.Timestamp

data class Post(val profile_picture: String = "",
                val profile_name: String = "",
                val post_timestamp: Timestamp? = null,
                val post_image: String = "",
                val post_description: String = "",
                val uid: String = "")