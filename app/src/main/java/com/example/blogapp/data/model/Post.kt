package com.example.blogapp.data.model

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Post(var id: String = "",
                val profile_picture: String = "",
                val profile_name: String = "",
                @ServerTimestamp
                var created_at: Date? = null,
                val post_image: String = "",
                val post_description: String = "",
                val uid: String = "",
                val likes: Int = 0)