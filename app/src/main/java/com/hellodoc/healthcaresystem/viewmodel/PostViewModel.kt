package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _posts = MutableStateFlow<List<PostResponse>>(emptyList())
    val posts: StateFlow<List<PostResponse>> get()= _posts

    fun getAllPosts(){
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.postService.getAllPosts()
                if (result.isSuccessful) {
                    _posts.value = result.body() ?: emptyList()
                } else {
                    println("Lỗi API: ${result.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("Lỗi ở getPostById")
                Log.e("Post: ", "Lỗi khi lấy Post: ${e.message}")
            }
        }
    }

    fun getUserById(id:String) {
        viewModelScope.launch {
            try {
                println("ID nhận được để lấy post: " + id)
                val result = RetrofitInstance.postService.getPostById(id)

                if (result.isSuccessful) {
                    _posts.value = result.body() ?: emptyList()
                    println("Kets qua: "+_posts.value)
                } else {
                    println("Lỗi API: ${result.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("Lỗi ở getPostById")
                Log.e("Ở Post:  ","Lỗi khi lấy Post: ${e.message}")            }
        }
    }
}
