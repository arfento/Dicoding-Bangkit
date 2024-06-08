package com.example.submssionstoryapp.view.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.submssionstoryapp.R
import com.example.submssionstoryapp.data.api.ApiConfig
import com.example.submssionstoryapp.data.model.FileUploadResponse
import com.example.submssionstoryapp.data.pref.UserPreference
import com.example.submssionstoryapp.data.pref.dataStore
import com.example.submssionstoryapp.databinding.ActivityStoryBinding
import com.example.submssionstoryapp.utils.getImageUri
import com.example.submssionstoryapp.utils.reduceFileImage
import com.example.submssionstoryapp.utils.uriToFile
import com.example.submssionstoryapp.view.main.MainActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var userPreference: UserPreference
    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        val description = binding.descriptionText.text.toString()

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                userPreference.getSession().collect { user ->
                    val token = user.token
                    if (token.isNotEmpty()) {
                        try {
                            val apiService = ApiConfig.getApiService(token)
                            val successResponse =
                                apiService.uploadStory("Bearer $token", multipartBody, requestBody)
                            showToast(successResponse.message)

                            AlertDialog.Builder(this@StoryActivity).apply {
                                setTitle(getString(R.string.success_upload))
                                setMessage(getString(R.string.succes_story_upload))
                                setPositiveButton(getString(R.string.next)) { _, _ ->
                                    val intent =
                                        Intent(this@StoryActivity, MainActivity::class.java).apply {
                                            flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }

                        } catch (e: HttpException) {
                            val errorBody = e.response()?.errorBody()?.string()
                            val errorResponse =
                                Gson().fromJson(errorBody, FileUploadResponse::class.java)
                            showToast(errorResponse.message)
                        } finally {
                            showLoading(false)
                        }
                    } else {
                        showToast(getString(R.string.token_toast))
                        showLoading(false)
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}