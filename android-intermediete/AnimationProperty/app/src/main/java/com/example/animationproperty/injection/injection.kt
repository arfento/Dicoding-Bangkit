package com.example.animationproperty.injection

import android.content.Context
import com.example.animationproperty.data.pref.UserPreferences
import com.example.animationproperty.data.pref.dataStore
import com.example.animationproperty.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}