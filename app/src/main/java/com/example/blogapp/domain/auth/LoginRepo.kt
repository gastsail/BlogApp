package com.example.blogapp.domain.auth

import com.example.blogapp.core.Resource
import com.google.firebase.auth.FirebaseUser

interface LoginRepo {
    suspend fun signIn(email:String,password:String): FirebaseUser?
}