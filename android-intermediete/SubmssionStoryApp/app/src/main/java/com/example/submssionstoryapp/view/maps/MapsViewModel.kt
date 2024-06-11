package com.example.submssionstoryapp.view.maps

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submssionstoryapp.data.model.ErrorResponse
import com.example.submssionstoryapp.data.model.ListStoryItem
import com.example.submssionstoryapp.data.model.UserModel
import com.example.submssionstoryapp.data.repository.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem?>>()
    val stories: LiveData<List<ListStoryItem?>> = _stories


    fun getStoryWithLocation(context: Context, location: Int = 1) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${userRepository.getSession().first().token}"
                val listStoryWithLocation = userRepository.getStoryWithLocation(token,location)
                _stories.value = listStoryWithLocation
                Log.d("MapsActivity", "Fetching stories with location: $location")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Toast.makeText(context, errorResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}