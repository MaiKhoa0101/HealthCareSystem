package com.hellodoc.healthcaresystem.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hellodoc.healthcaresystem.api.PostResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel : ViewModel() {
    private val _posts = mutableStateOf<List<PostResponse>>(emptyList())
    val posts: State<List<PostResponse>> = _posts

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        RetrofitInstance.postService.getPosts().enqueue(object : Callback<List<PostResponse>> {
            override fun onResponse(
                call: Call<List<PostResponse>>,
                response: Response<List<PostResponse>>
            ) {
                if (response.isSuccessful) {
                    _posts.value = response.body() ?: emptyList()
                }
            }

            override fun onFailure(call: Call<List<PostResponse>>, t: Throwable) {
                Log.e("PostViewModel", "Failed to load posts", t)
            }
        })
    }
}
