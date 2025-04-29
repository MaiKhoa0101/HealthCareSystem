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
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
