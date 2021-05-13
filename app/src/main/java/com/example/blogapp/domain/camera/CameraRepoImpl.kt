package com.example.blogapp.domain.camera

import android.graphics.Bitmap
import com.example.blogapp.data.remote.camera.CameraDatasource

class CameraRepoImpl(private val datasource: CameraDatasource): CameraRepo {
    override suspend fun uploadPhoto(imageBitmap: Bitmap, description: String): Boolean {
        return datasource.uploadPhoto(imageBitmap, description)
    }
}