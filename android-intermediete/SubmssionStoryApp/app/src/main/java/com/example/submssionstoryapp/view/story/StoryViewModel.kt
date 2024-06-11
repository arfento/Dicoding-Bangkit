package com.example.submssionstoryapp.view.story

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submssionstoryapp.data.model.FileUploadResponse
import com.example.submssionstoryapp.data.repository.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class StoryViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var myLat: RequestBody? = null
    private var myLon: RequestBody? = null
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _isResponse = MutableLiveData<Boolean>()
    fun uploadStory(
        context: Context,
        desc: RequestBody,
        photo: MultipartBody.Part,
        lat: Double? = null,
        lon: Double? = null
    ) {

        _isLoading.value = true
        viewModelScope.launch {
            userRepository.getSession().collect { user ->
                val token = user.token
                try {
                    if (lat != null && lon != null) {
                        myLat = lat.toString().toRequestBody("text/plain".toMediaType())
                        myLon = lon.toString().toRequestBody("text/plain".toMediaType())
                    }
                    val response =
                        userRepository.uploadStory(token, photo, desc, myLat, myLon)

                    _isResponse.value = !response.error


                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
                    Toast.makeText(context, errorResponse.message, Toast.LENGTH_SHORT).show()
                } finally {
                    _isLoading.value = false
                }
            }
        }

    }


}