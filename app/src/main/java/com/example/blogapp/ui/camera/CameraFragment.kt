package com.example.blogapp.ui.camera

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blogapp.R
import com.example.blogapp.core.Result
import com.example.blogapp.data.remote.camera.CameraDatasource
import com.example.blogapp.databinding.FragmentCameraBinding
import com.example.blogapp.domain.camera.CameraRepoImpl

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var binding: FragmentCameraBinding
    private val viewModel by viewModels<CameraViewModel> { CameraViewModelFactory(CameraRepoImpl(CameraDatasource())) }
    private var bitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No se encontro ninguna app para abrir la camara.", Toast.LENGTH_SHORT).show()
        }

        binding.btnUploadPhoto.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext()).setTitle("Uploading...").create()
            bitmap?.let {
                viewModel.uploadPhoto(it, binding.etxPhotoDescription.text.toString().trim()).observe(viewLifecycleOwner, {
                    when (it) {
                        is Result.Loading -> {
                            alertDialog.show()
                        }
                        is Result.Success -> {
                            alertDialog.dismiss()
                            findNavController().popBackStack()
                        }
                        is Result.Failure -> {
                            alertDialog.dismiss()
                        }
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imgAddPhoto.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
        }
    }

}