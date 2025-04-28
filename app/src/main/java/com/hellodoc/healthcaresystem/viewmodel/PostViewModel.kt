package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.api.PostResponse
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.responsemodel.CreatePostResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostViewModel(sharedPreferences: SharedPreferences) : ViewModel() {
    private val _posts = mutableStateOf<List<PostResponse>>(emptyList())
    val posts: State<List<PostResponse>> = _posts

    private val _createPostResponse = MutableLiveData<CreatePostResponse>()
    val postResponse: LiveData<CreatePostResponse> get() = _createPostResponse

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

    fun createPost(request: CreatePostRequest, context: Context) {
        viewModelScope.launch {
            try {
                val userIdPart = MultipartBody.Part.createFormData("userId", request.userId)
                val contentPart = MultipartBody.Part.createFormData("content", request.content)

                val imageParts = request.images?.mapNotNull { uri ->
                    prepareFilePart(context, uri, "images")
                }

                val response = RetrofitInstance.postService.createPost(
                    userIdPart,
                    contentPart,
                    imageParts ?: emptyList()
                )

                if (response.isSuccessful) {
                    _createPostResponse.value = response.body()
                } else {
                    Log.e("PostViewModel", "Create Post Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Create Post Exception", e)
            }
        }
    }

    private fun prepareFilePart(context: Context, fileUri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
        } catch (e: Exception) {
            Log.e("PostViewModel", "Error preparing file part", e)
            null
        }
    }



}
