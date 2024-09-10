package com.novumlogic.bookmatch.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.novumlogic.bookmatch.utils.GoogleSignInHelper

class ViewModelFactory(private val signInHelper: GoogleSignInHelper): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(signInHelper) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}