package com.example.blogapp.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Post(
                @Exclude @JvmField
                var id: String = "",
                val profile_picture: String = "",
                val profile_name: String = "",
                @ServerTimestamp
                var created_at: Date? = null,
                val post_image: String = "",
                val post_description: String = "",
                val poster: Poster? = null,
                var likes: Long = 0,
                @Exclude @JvmField
                var liked: Boolean = false)


data class Poster(val username: String? = "", val uid: String? = "")