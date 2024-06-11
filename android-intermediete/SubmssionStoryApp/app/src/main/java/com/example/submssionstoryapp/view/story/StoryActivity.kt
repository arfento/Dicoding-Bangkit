package com.example.submssionstoryapp.view.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.submssionstoryapp.R
import com.example.submssionstoryapp.ViewModelFactory
import com.example.submssionstoryapp.data.pref.UserPreference
import com.example.submssionstoryapp.data.pref.dataStore
import com.example.submssionstoryapp.databinding.ActivityStoryBinding
import com.example.submssionstoryapp.utils.getImageUri
import com.example.submssionstoryapp.utils.reduceFileImage
import com.example.submssionstoryapp.utils.uriToFile
import com.example.submssionstoryapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null
    private var currentImageUri: Uri? = null
    private lateinit var userPreference: UserPreference


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.request_granted), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.request_denied), Toast.LENGTH_LONG).show()
        }
    }

    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        userPreference = UserPreference.getInstance(dataStore)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.apply {
            checkBoxLocation.setOnClickListener {
                if (!checkBoxLocation.isChecked) {
                    lat = null
                    lon = null
                    checkBoxLocation.isChecked = false
                } else {
                    checkBoxLocation.isChecked = true
                    requestLocatedPermission()
                }
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener {
            uploadPhoto()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun uploadPhoto() {
        showLoading(true)
        val desc = binding.descriptionText.text.toString()
        if (currentImageUri != null && desc.isNotEmpty()) {
            val imageFile = uriToFile(currentImageUri!!, this).reduceFileImage()
            val requestBody = desc.toRequestBody("text/plain".toMediaType())
            val reqImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                reqImageFile
            )
            viewModel.uploadStory(
                this@StoryActivity,
                requestBody,
                multipartBody,
                lat,
                lon
            )
            moveToMain()
        } else {
            showToast(getString(R.string.empty_error))
            showLoading(false)

        }
    }


    private fun moveToMain() {
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
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            binding.checkBoxLocation.isActivated = false
            return
        }

        fusedLocation.lastLocation.addOnSuccessListener { mylocation ->
            lat = mylocation.latitude
            lon = mylocation.longitude
        }

    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocatedPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            showToast("Location permission is needed for accessing location.")
            binding.checkBoxLocation.isChecked = false
        } else {
            getMyLocation()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherCamera.launch(currentImageUri!!)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
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

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val REQUEST_CODE = 101
    }

}