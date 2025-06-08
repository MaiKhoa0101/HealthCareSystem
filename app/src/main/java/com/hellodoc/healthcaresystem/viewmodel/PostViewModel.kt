package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.CreateCommentPostRequest
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateFavoritePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdatePostRequest
import com.hellodoc.healthcaresystem.responsemodel.CreatePostResponse
import com.hellodoc.healthcaresystem.responsemodel.GetCommentPostResponse
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.ManagerResponse
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
    val posts: StateFlow<List<PostResponse>> = _posts

    private val _hasMorePosts = MutableStateFlow(true)
    val hasMorePosts: StateFlow<Boolean> = _hasMorePosts

    private val _isLoadingMorePosts = MutableStateFlow(false)
    val isLoadingMorePosts: StateFlow<Boolean> = _isLoadingMorePosts



    private val _createPostResponse = MutableLiveData<CreatePostResponse>()

    suspend fun fetchPosts(skip: Int = 0, limit: Int = 10, append: Boolean = false): Boolean {
        return try {
            val response = RetrofitInstance.postService.getAllPosts(skip, limit)
            if (response.isSuccessful) {
                println("Capa nhat post moi: "+response.body())
                val result = response.body()
                val newPosts = result?.posts ?: emptyList()
                val hasMore = result?.hasMore ?: false

                if (append) {
                    val current = _posts.value
                    _posts.value = current + newPosts
                } else {
                    _posts.value = newPosts
                }
                _hasMorePosts.value = hasMore
                true
            } else {
                Log.e("PostViewModel", "Lỗi API: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("PostViewModel", "Post Fetch Error", e)
            false
        }
    }



    private val _commentsMap = MutableStateFlow<Map<String, List<GetCommentPostResponse>>>(emptyMap())
    val commentsMap: StateFlow<Map<String, List<GetCommentPostResponse>>> = _commentsMap

    private val _hasMoreMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val hasMoreMap: StateFlow<Map<String, Boolean>> = _hasMoreMap

    suspend fun fetchComments(postId: String, skip: Int = 0, limit: Int = 10, append: Boolean = false): Boolean {
        return try {
            val response = RetrofitInstance.postService.getCommentByPostId(postId, skip, limit)
            if (response.isSuccessful) {
                val result = response.body()
                val comments = result?.comments ?: emptyList()
                val hasMore = result?.hasMore ?: false

                if (append) {
                    val current = _commentsMap.value[postId] ?: emptyList()
                    _commentsMap.value += (postId to (current + comments))
                } else {
                    _commentsMap.value += (postId to comments)
                }
                _hasMoreMap.value += (postId to hasMore)
                true
            } else {
                Log.e("PostViewModel", "Lỗi API: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("PostViewModel", "Comment Fetch Error", e)
            false
        }
    }

    fun getPostById(id:String) {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.postService.getPostById(id)

                if (result.isSuccessful) {
                    result.body()?.let {
                        _posts.value = listOf(it)
                    } ?: run {
                        _posts.value = emptyList()
                    }
                    println("Kết qua getPostById: "+_posts.value)
                } else {
                    println("Lỗi API: ${result.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("Lỗi ở getPostById")
                Log.e("Ở Post:  ","Lỗi khi lấy Post: ${e.message}")            }
        }
    }

    fun getPostByUserId(userId:String) {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.postService.getPostByUserId(userId)

                if (result.isSuccessful) {
                    _posts.value = result.body() ?: emptyList()
                    println("Kết qua getPostByUserId: "+_posts.value)
                } else {
                    println("Lỗi API: ${result.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("Lỗi ở getPostByUserId")
                Log.e("Ở Post:  ","Lỗi khi lấy Post: ${e.message}")            }
        }
    }

    fun createPost(request: CreatePostRequest, context: Context) {
        viewModelScope.launch {
            try {
                val userIdPart = MultipartBody.Part.createFormData("userId", request.userId)
                val userModelPart = MultipartBody.Part.createFormData("userModel", request.userModel)
                val contentPart = MultipartBody.Part.createFormData("content", request.content)

                val imageParts = request.images?.mapNotNull { uri ->
                    prepareFilePart(context, uri, "images")
                }

                val response = RetrofitInstance.postService.createPost(
                    userIdPart,
                    userModelPart,
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

    fun sendComment(postId: String, userId: String, userModel: String, content: String) {
        viewModelScope.launch {
            try {
                Log.d("sendComment", "➡ Gửi comment với postId=$postId, userId=$userId, userModel=$userModel, content=$content")

                val response = RetrofitInstance.postService.createCommentByPostId(
                    postId,
                    CreateCommentPostRequest(userId, userModel, content)
                )
                Log.d("sendComment", "⬅ Response code: ${response.code()}")

                if (response.isSuccessful) {
                    Log.d("sendComment", " Gửi comment thành công")
                    fetchComments(postId)
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.e("sendComment", " Gửi comment thất bại: $errorMsg")
                }
            } catch (e: Exception) {
                Log.e("sendComment", "Lỗi ngoại lệ khi gửi comment", e)
            }
        }
    }

    fun updateComment(commentId: String, userId: String, userModel: String, content: String) {
        viewModelScope.launch {
            try {
                RetrofitInstance.postService.updateCommentById(
                    commentId,
                    CreateCommentPostRequest(userId, userModel, content)
                )
            } catch (e: Exception) {
                Log.e("PostViewModel", "Update Comment Error", e)
            }
        }
    }
    
    private val _userComments = MutableStateFlow<List<ManagerResponse>>(emptyList())
    val userComments: StateFlow<List<ManagerResponse>> get()= _userComments

    fun getPostCommentByUserId(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.getCommentByUserId(userId)
                if (response.isSuccessful) {
                    _userComments.value = response.body() ?: emptyList()
                    Log.d("userCommentManager", "Get user comment success")
                } else {
                    Log.e("userCommentManager", "Get user comment failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("userCommentManager", "Get user comment error: ${e.message}")
            }
        }
    }

    fun deleteComment(commentId: String, postId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.deleteCommentById(commentId)
                if (response.isSuccessful) {
                    fetchComments(postId) // Refresh danh sách bình luận
                } else {
                    Log.e("PostViewModel", "Delete comment failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Delete Comment Error", e)
            }
        }
    }
    
    private val _userFavorites = MutableStateFlow<List<ManagerResponse>>(emptyList())
    val userFavorites: StateFlow<List<ManagerResponse>> get()= _userFavorites

    fun getPostFavoriteByUserId(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.getUserFavoritePost(userId)
                if (response.isSuccessful) {
                    _userFavorites.value = response.body() ?: emptyList()
                    Log.d("userFavoriteManager", "Get user favorite success")
                } else {
                    Log.e("userFavoriteManager", "Get user favorite failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("userFavoriteManager", "Get user favorite error: ${e.message}")
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.postService.deletePostById(postId)
                if (response.isSuccessful) {
                    fetchPosts() // cập nhật lại danh sách
                } else {
                    Log.e("PostViewModel", "Delete post failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Delete Post Error", e)
            }
        }
    }

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    fun updatePost(postId: String, request: UpdatePostRequest, context: Context) {
        viewModelScope.launch {
            try {
                _isUpdating.value = true

                val contentPart = MultipartBody.Part.createFormData("content", request.content)
                val imageParts = request.images.mapNotNull { uri ->
                    prepareFilePart(context, uri, "images")
                }

                val response = RetrofitInstance.postService.updatePost(
                    postId = postId,
                    content = contentPart,
                    images = imageParts
                )

                if (response.isSuccessful) {
                    _updateSuccess.value = true
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Update Post Exception", e)
            } finally {
                _isUpdating.value = false
                println("Đổi biến trc")
            }
        }
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }




    private val _activePostMenuId = MutableStateFlow<String?>(null)
    val activePostMenuId: StateFlow<String?> get() = _activePostMenuId

    fun togglePostMenu(postId: String) {
        _activePostMenuId.value = if (_activePostMenuId.value == postId) null else postId
    }

    fun closeAllPostMenus() {
        _activePostMenuId.value = null
    }


}
