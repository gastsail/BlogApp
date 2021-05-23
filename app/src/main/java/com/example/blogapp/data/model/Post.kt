package com.example.blogapp.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Post(val profile_picture: String = "",
                val profile_name: String = "",
                @ServerTimestamp var post_timestamp: Date? = null,
                val post_image: String = "",
                val post_description: String = "",
                val uid: String = "")