package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hellodoc.healthcaresystem.requestmodel.CreateCommentPostRequest
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.requestmodel.GetFavoritePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateFavoritePostRequest
import com.hellodoc.healthcaresystem.responsemodel.CommentResponse
import com.hellodoc.healthcaresystem.responsemodel.CreatePostResponse
import com.hellodoc.healthcaresystem.responsemodel.GetCommentPostResponse
import com.hellodoc.healthcaresystem.responsemodel.GetFavoritePostResponse
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.UpdateFavoritePostResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PostViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _posts = MutableStateFlow<List<PostResponse>>(emptyList())
    val posts: StateFlow<List<PostResponse>> get()= _posts

    private val _createPostResponse = MutableLiveData<CreatePostResponse>()
    val postResponse: LiveData<CreatePostResponse> get() = _createPostResponse

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

    fun getPostUserById(id:String) {
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

    fun updateCommentPost(postId: String, userId: String) {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.postService.updateCommentPost(
//                    postId, mapOf("userId" to userId)
//                )
//                if (response.isSuccessful) {
//                    getUserById(userId) // chỉ refresh bài viết của đúng user đang xem
//                }
//            } catch (e: Exception) {
//                Log.e("PostViewModel", "Like Post Error", e)
//            }
//        }
    }

    private val _isFavoritedMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isFavoritedMap: StateFlow<Map<String, Boolean>> get() = _isFavoritedMap

    private val _totalFavoritesMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val totalFavoritesMap: StateFlow<Map<String, String>> get() = _totalFavoritesMap

    fun fetchFavoriteForPost(postId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.getFavoriteByPostId(postId, userId)
                if (response.isSuccessful) {
                    val isFavorited = response.body()?.isFavorited ?: false
                    val totalFavorites = response.body()?.totalFavorites?.toString() ?: "0"
                    _isFavoritedMap.value += (postId to isFavorited)
                    _totalFavoritesMap.value += (postId to totalFavorites)
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetchFavoriteForPost", e)
            }
        }
    }

    fun updateFavoriteForPost(postId: String, userId: String, userModel: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.updateFavoriteByPostId(postId, UpdateFavoritePostRequest(userId, userModel))
                if (response.isSuccessful) {
                    val isFavorited = response.body()?.isFavorited ?: false
                    val totalFavorites = response.body()?.totalFavorites?.toString() ?: "0"
                    _isFavoritedMap.value += (postId to isFavorited)
                    _totalFavoritesMap.value += (postId to totalFavorites)
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error updateFavoriteForPost", e)
            }
        }
    }

    private val _commentsMap = MutableStateFlow<Map<String, List<GetCommentPostResponse>>>(emptyMap())
    val commentsMap: StateFlow<Map<String, List<GetCommentPostResponse>>> get() = _commentsMap

    fun fetchComments(postId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.getCommentByPostId(postId)
                if (response.isSuccessful) {
                    val comments = response.body() ?: emptyList()
                    _commentsMap.value += (postId to comments)
                } else {
                    Log.e("PostViewModel", "Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Comment Fetch Error", e)
            }
        }
    }

    fun sendComment(postId: String, userId: String, userModel: String, content: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.createCommentByPostId(
                    postId,
                    CreateCommentPostRequest(userId, userModel, content)
                )
                if (response.isSuccessful) {
                    fetchComments(postId) // refresh
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Send Comment Error", e)
            }
        }
    }

    suspend fun fetchCommentsForPost(postId: String): List<GetCommentPostResponse> {
        return try {
            val response = RetrofitInstance.postService.getCommentByPostId(postId)
            if (response.isSuccessful) response.body() ?: emptyList()
            else emptyList()
        } catch (e: Exception) {
            Log.e("PostViewModel", "Lỗi khi fetch comments", e)
            emptyList()
        }
    }

    private val _userComments = MutableStateFlow<List<CommentResponse>>(emptyList())
    val userComments: StateFlow<List<CommentResponse>> get()= _userComments

    fun getPostCommentByUserId(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.getCommentByUserId(userId)
                if (response.isSuccessful) {
                    _userComments.value = response.body() ?: emptyList()
                    Log.d("tag", "Get user comment success")
                } else {
                    Log.e("tag", "Get user comment failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("tag", "Get user comment error: ${e.message}")
            }
        }
    }



}
