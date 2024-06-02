package com.example.submssionstoryapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submssionstoryapp.data.model.LoginResponse
import com.example.submssionstoryapp.data.repository.UserRepository
import com.example.submssionstoryapp.data.model.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> get() = _isError

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse
    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                setAuth(
                    UserModel(
                        response.loginResult.userId,
                        response.loginResult.name,
                        email,
                        response.loginResult.token,
                        true
                    )
                )
                _isLoading.postValue(false)
                _loginResponse.postValue(response)
            } catch (e: HttpException) {
                _isLoading.postValue(false)
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
                val errorMessage = errorBody.message
                _isError.postValue(errorMessage)
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _isError.postValue(e.message ?: "An unexpected error occurred")
            }
        }
    }

    private fun setAuth(userModel: UserModel) {
        viewModelScope.launch {
            userRepository.setAuth(userModel)
        }
    }

}