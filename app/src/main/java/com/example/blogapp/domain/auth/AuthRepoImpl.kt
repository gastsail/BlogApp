package com.example.blogapp.domain.auth

import com.example.blogapp.data.remote.auth.AuthDataSource
import com.google.firebase.auth.FirebaseUser

class AuthRepoImpl(private val dataSource: AuthDataSource) : AuthRepo {
    override suspend fun signIn(email: String, password: String): FirebaseUser? =
        dataSource.signIn(email, password)

    override suspend fun signUp(email: String, password: String, username: String): FirebaseUser? = dataSource.signUp(email,password,username)
}