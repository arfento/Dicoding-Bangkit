package com.example.submssionstoryapp.data.repository

import com.example.submssionstoryapp.data.api.ApiService
import com.example.submssionstoryapp.data.model.LoginResponse
import com.example.submssionstoryapp.data.model.RegisterResponse
import com.example.submssionstoryapp.data.model.StoryResponse
import com.example.submssionstoryapp.data.model.UserModel
import com.example.submssionstoryapp.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(private val userPreference: UserPreference, private val apiService: ApiService) {

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }
    suspend fun logout() {
        userPreference.logout()
    }
    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun getStory(token: String): StoryResponse {
        return apiService.getStory(token)
    }
    suspend fun setAuth(user: UserModel) = userPreference.saveSession(user)

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