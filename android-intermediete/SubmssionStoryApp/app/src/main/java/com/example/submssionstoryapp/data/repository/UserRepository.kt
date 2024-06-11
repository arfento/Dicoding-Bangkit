package com.example.submssionstoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submssionstoryapp.adapter.StoryPagingSource
import com.example.submssionstoryapp.data.api.ApiService
import com.example.submssionstoryapp.data.model.FileUploadResponse
import com.example.submssionstoryapp.data.model.ListStoryItem
import com.example.submssionstoryapp.data.model.LoginResponse
import com.example.submssionstoryapp.data.model.RegisterResponse
import com.example.submssionstoryapp.data.model.UserModel
import com.example.submssionstoryapp.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun setAuth(user: UserModel) = userPreference.saveSession(user)


    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun getStoryWithLocation(token: String, location: Int = 1): List<ListStoryItem?> {
        return apiService.getStoriesWithLocation(token, location).listStory
    }


    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, userPreference)
            }
        ).liveData
    }

    suspend fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): FileUploadResponse {
        return apiService.uploadStory("Bearer $token", image, description, latitude, longitude)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}