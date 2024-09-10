package com.novumlogic.bookmatch.screens.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novumlogic.bookmatch.screens.dispatcherIO
import data.RemoteDataSource
import kotlinx.coroutines.launch

class ContentViewModel: ViewModel() {
    private val TAG = javaClass.simpleName


    fun changeLikeDislike(recommendedBookId: Int, value: Boolean?, currentTimeMillis: Long) {
        viewModelScope.launch(dispatcherIO) {
            val status = RemoteDataSource.changeLikeDislike(recommendedBookId,value,currentTimeMillis)
            if(status) Log.d(TAG, "changeLikeDislike: $recommendedBookId Updated successfully to $value")
            else Log.d(TAG, "changeLikeDislike: Update failed for $recommendedBookId and value = $value")
        }
    }

    fun updateRatings(recommendedBookId: Int, rating: Int?, currentTimeMillis: Long) {
        viewModelScope.launch(dispatcherIO) {
            val status = RemoteDataSource.updateRatings(recommendedBookId,rating,currentTimeMillis)
            if(status) Log.d(TAG, "updateRatings: $recommendedBookId = $rating successfully")
            else Log.d(TAG, "updateRatings: failed for $recommendedBookId and value = $rating")
          }
    }

    fun updateReadStatus(recommendBookId: Int, status: Boolean, currentTimeMillis: Long) {
        viewModelScope.launch(dispatcherIO) {
            val result = RemoteDataSource.updateReadStatus(recommendBookId,status, currentTimeMillis)
            if(result) Log.d(TAG, "updateReadStatus: $recommendBookId = $status successfully")
            else Log.d(TAG, "updateReadStatus: $recommendBookId = $status failed ")
        }
    }
}
