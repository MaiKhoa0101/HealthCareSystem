package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.Content
import com.hellodoc.healthcaresystem.requestmodel.CreateCommentPostRequest
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.requestmodel.GeminiRequest
import com.hellodoc.healthcaresystem.requestmodel.Part
import com.hellodoc.healthcaresystem.requestmodel.UpdateFavoritePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdatePostRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.CreatePostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.CommentPostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ManagerResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UiState
import com.hellodoc.healthcaresystem.model.repository.GeminiRepository
import com.hellodoc.healthcaresystem.model.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicInteger
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.map
import kotlin.collections.orEmpty
import kotlin.collections.plus
import kotlin.text.trim


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val geminiRepository: GeminiRepository,
    private val sharedPreferences: SharedPreferences,
    private val geminiHelper: GeminiHelper


    ) : ViewModel() {
    private val _posts = MutableStateFlow<List<PostResponse>>(emptyList())
    val posts: StateFlow<List<PostResponse>> = _posts

    private val _post = MutableStateFlow<PostResponse?>(null) // nullable
    val post: StateFlow<PostResponse?> = _post

    private val _hasMorePosts = MutableStateFlow(true)
    val hasMorePosts: StateFlow<Boolean> = _hasMorePosts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _isLoadingMorePosts = MutableStateFlow(false)
    val isLoadingMorePosts: StateFlow<Boolean> = _isLoadingMorePosts

    private val _isAnalyzingKeywords = MutableStateFlow(false)
    val isAnalyzingKeywords: StateFlow<Boolean> get() = _isAnalyzingKeywords

    private val _extractedKeywords = MutableStateFlow<List<String>>(emptyList())
    val extractedKeywords: StateFlow<List<String>> get() = _extractedKeywords

    private val _createPostResponse = MutableLiveData<CreatePostResponse>()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private var _uiStatePost = MutableStateFlow<UiState>(UiState.Idle)
    val uiStatePost: StateFlow<UiState> = _uiStatePost
    fun loadInitialPosts() {
        viewModelScope.launch {
            clearPosts()
            fetchPosts(skip = 0, limit = 10, append = false)
        }
    }

    fun loadMorePosts(currentSkip: Int, limit: Int = 10) {
        if (isLoadingMorePosts.value || !hasMorePosts.value) return
        viewModelScope.launch {
            fetchPosts(skip = currentSkip, limit = limit, append = true)
        }
    }

    suspend fun fetchPosts(skip: Int = 0, limit: Int = 10, append: Boolean = false): Boolean {
        if (_isLoading.value) return false
        _isLoading.value = true
        return try {
            val response = postRepository.getAllPosts(skip, limit)
            if (response.isSuccessful) {
                println("Get post: "+response.body())
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
        finally {
            _isLoading.value = false
        }
    }

    suspend fun getPostByUserId(
        userId: String,
        skip: Int = 0,
        limit: Int = 10,
        append: Boolean = false
    ):Boolean {
        if (_isLoading.value) return false
        _isLoading.value = true

        return try {
            val response = postRepository.getPostByUserId(userId, skip, limit)

            if (response.isSuccessful) {
                println("Get post: " + response.body())
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
        } finally {
            _isLoading.value = false
        }
    }

    private val _commentsMap = MutableStateFlow<Map<String, List<CommentPostResponse>>>(emptyMap())
    val commentsMap: StateFlow<Map<String, List<CommentPostResponse>>> = _commentsMap

    private val _hasMoreMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val hasMoreMap: StateFlow<Map<String, Boolean>> = _hasMoreMap

    suspend fun fetchComments(postId: String, skip: Int = 0, limit: Int = 10, append: Boolean = false): Boolean {
        return try {
            val response = postRepository.getCommentByPostId(postId, skip, limit)
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

//    fun getPostById(id: String) {
//        if (_isLoading.value) return
//
//        _isLoading.value = true
//        viewModelScope.launch {
//            try {
//                val result = RetrofitInstance.postService.getPostById(id)
//                if (result.isSuccessful) {
//                    _post.value = result.body()
//                } else {
//                    _errorMessage.value = "Failed to load post: ${result.code()}"
//                }
//            } catch (e: Exception) {
//                _errorMessage.value = "Network error: ${e.localizedMessage}"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

    fun getPostById(id: String, context: Context) {
        if (_isLoading.value) return

        _isLoading.value = true
        // Reset post cũ trước khi load mới
        _post.value = null
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                // 1) Lấy post chi tiết
                val postResult = withContext(Dispatchers.IO) {
                    postRepository.getPostById(id)
                }

                if (!postResult.isSuccessful) {
                    _errorMessage.value = "Failed to load post: ${postResult.code()}"
                    return@launch
                }

                val post = postResult.body()
                if (post == null) {
                    _errorMessage.value = "Post data is null"
                    return@launch
                }

                // Gán post mới để UI hiển thị ngay
                _post.value = post

                // 2) Kiểm tra keywords
                var finalKeywords = post.keywords
                if (finalKeywords.isNullOrEmpty()) {
                    Log.d("PostViewModel", "Post chưa có keywords, đang tạo mới...")
                    finalKeywords = generateAndUpdateKeywords(post, context)
                    // Sau khi generate xong có thể update lại _post.value nếu backend trả keywords mới
                    _post.value = _post.value?.copy(keywords = finalKeywords)
                } else {
                    Log.d("PostViewModel", "Post đã có keywords: $finalKeywords")
                }

                // 3) Kiểm tra embedding
                val embeddingResponse = post.embedding
                Log.d("PostViewModel", "hasEmbedding response: $embeddingResponse")

                if (embeddingResponse.isNullOrEmpty() && !finalKeywords.isNullOrEmpty()) {
                    Log.d("PostViewModel", "Post không có embedding, đang tạo embedding mới...")
                    generateEmbedding(id, finalKeywords)
                }

            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
                Log.e("PostViewModel", "Error fetching post by id", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    private suspend fun generateEmbedding(id: String, keywords: String) {
        try{
            val response = postRepository.createEmbedding(id, keywords)
            if (response.isSuccessful) {
                Log.d("PostViewModel", "Tạo embedding thành công cho post $id")
            } else {
                Log.e("PostViewModel", "Lỗi tạo embedding: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("PostViewModel", "Exception khi tạo embedding", e)
        }
    }

    private suspend fun generateAndUpdateKeywords(post: PostResponse, context: Context): String? {
        return try {

            var contentKeywords= emptyList<String>()
            // Phân tích từ khóa từ nội dung text
            post.content?.let { content ->
                contentKeywords = analyzeContentKeywords(content)
            }

            val mediaUri = post.media.orEmpty()

            val mediaKeywords = if (mediaUri.isNotEmpty()) {
                geminiHelper.readImageAndVideoFromInternet(context, mediaUri)
            } else emptyList()



            val allKeywords = (contentKeywords + mediaKeywords)
                .map { it.trim() }.filter { it.isNotEmpty() }.distinct().take(10)

            // Loại bỏ trùng lặp và giới hạn số lượng
            val finalKeywords = allKeywords
                .map { it.trim() }.filter { it.isNotEmpty() }.distinct().take(10)


            if (finalKeywords.isNotEmpty()) {

                Log.d("PostViewModel", "keyWord mới được tạo: $finalKeywords")
                // Cập nhật keywords cho post
                updatePostKeywords(post.id, finalKeywords)

                // Cập nhật post hiện tại với keywords mới
                _post.value = post.copy(keywords = finalKeywords.toString())

                Log.d(
                    "PostViewModel",
                    "Đã tạo và cập nhật ${finalKeywords.size} từ khóa cho post ${post.id}"
                )
            } else{
                null
            }

        } catch (e: Exception) {
            Log.e("PostViewModel", "Lỗi khi tạo keywords cho post: ${e.localizedMessage}")
            null
        }.toString()
    }
    data class UpdateKeywordsRequest(
        val keywords: String
    )


    private suspend fun updatePostKeywords(postId: String, keywords: List<String>) {
        try {
            val request = UpdateKeywordsRequest(keywords.joinToString(","))
            val response = postRepository.addKeywords(postId, request)

            if (response.isSuccessful) {
                Log.d("PostViewModel", "Cập nhật keywords thành công cho post $postId")
            } else {
                Log.e("PostViewModel", "Lỗi cập nhật keywords: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("PostViewModel", "Exception khi cập nhật keywords", e)
        }
    }





    private suspend fun analyzeContentKeywords(content: String): List<String> {
        return try {
            _isAnalyzingKeywords.value = true

            // Chia nhỏ content để không vượt quá giới hạn
            val chunks = content.chunked(300) // mỗi chunk tối đa 300 ký tự
            val allKeywords = mutableSetOf<String>()

            for (chunk in chunks) {
                val keywordPrompt = """
                Trích xuất 5-10 từ khóa y tế từ đoạn văn sau:
                "$chunk"
                
                Nhiệm vụ của bạn: phân tích và trích xuất từ khóa mô tả nội dung 
                    Yêu cầu: 
                        - Mỗi từ khóa viết trên một dòng. 
                        - Viết thường (lowercase).
                        - Chỉ gồm ký tự chữ cái và số, không dấu chấm câu, không ký tự đặc biệt.
                        - Mỗi từ khóa phải có cả tiếng Việt và tiếng Anh, cách nhau bằng dấu phẩy. 
                        - Không được trả lời gì ngoài từ khóa.
                        - Nếu không có từ khóa phù hợp, không trả lời gì
            """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = keywordPrompt))))
                )

                var attempts = 0
                val maxAttempts = ApiKeyManager.getTotalKeys()

                while (attempts < maxAttempts) {
                    val apiKey = ApiKeyManager.getCurrentKey()
                    try {
                        val response = geminiRepository.askGemini(apiKey, request)

                        if (response.isSuccessful && !response.body()?.candidates.isNullOrEmpty()) {
                            val aiResponse = response.body()!!.candidates.first().content.parts.first().text
                            val keywords = aiResponse.split("\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                            allKeywords.addAll(keywords)
                        } else {
                            Log.e(
                                "GeminiHelper",
                                "Lỗi hệ thống với API key: $apiKey - ${response.code()} ${response.errorBody()?.string()}"
                            )
                            ApiKeyManager.rotateKey()
                            attempts++
                        }
                    } catch (e: Exception) {
                        Log.e("GeminiHelper", "Lỗi kết nối với API key: $apiKey - ${e.localizedMessage}")
                        ApiKeyManager.rotateKey()
                        attempts++
                    }
                }
            }

            val finalKeywords = allKeywords.take(10).toList()
            _extractedKeywords.value = finalKeywords
            finalKeywords

        } catch (e: Exception) {
            Log.e("KeywordAnalysis", "Error analyzing keywords", e)
            emptyList()
        } finally {
            _isAnalyzingKeywords.value = false
        }
    }


    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

    fun createPost(request: CreatePostRequest, context: Context) {
        viewModelScope.launch {
            _uploadProgress.value = 0f
            _uiStatePost.value = UiState.Loading

            try {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Đang đăng bài...", Toast.LENGTH_SHORT).show()
                }

                // 1) Phân tích từ khóa
                val contentKeywords = async(Dispatchers.Default) {analyzeContentKeywords(request.content)}
                val mediaUri = request.media

                val mediaKeywords = async (Dispatchers.IO){
                    if (mediaUri.isNotEmpty()) {
                        geminiHelper.readImageAndVideo(context, mediaUri)
                    }
                    else emptyList()
                }
                val allKeywords = (contentKeywords.await() + mediaKeywords.await())
                    .map { it.trim() }.filter { it.isNotEmpty() }.distinct().take(10)

                // 2) Parts text
                val userIdPart    = MultipartBody.Part.createFormData("userId", request.userId)
                val userModelPart = MultipartBody.Part.createFormData("userModel", request.userModel)
                val contentPart   = MultipartBody.Part.createFormData("content", request.content)
                val keywordsPart  = if (allKeywords.isNotEmpty())
                    MultipartBody.Part.createFormData("keywords", allKeywords.joinToString(","))
                else null

                // 3) Chuẩn bị danh sách file HỢP LỆ trước (để tính totalFiles CHÍNH XÁC)
                val uris = request.media

                data class Tmp(val file: File, val mime: String)
                val tmpFiles: List<Tmp> = withContext(Dispatchers.IO) {
                    uris.mapNotNull { uri ->
                        try {
                            val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
                            val f = File.createTempFile("upload_", null, context.cacheDir)
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                FileOutputStream(f).use { out -> input.copyTo(out) }
                            } ?: run {
                                f.delete(); return@mapNotNull null
                            }
                            Tmp(f, mime)
                        } catch (e: Exception) {
                            Log.e("PostViewModel", "Copy uri -> temp fail: $uri", e)
                            null
                        }
                    }
                }

                val totalFiles = tmpFiles.size
                val imageParts: List<MultipartBody.Part> = if (totalFiles == 0) {
                    emptyList()
                } else {
                    val uploaded = AtomicInteger(0)
                    tmpFiles.map { tmp ->
                        val body = FileCompletionRequestBody(
                            file = tmp.file,
                            mediaType = tmp.mime.toMediaTypeOrNull()
                        ) {
                            val done = uploaded.incrementAndGet()
                            // xóa file tạm sau khi file này upload xong để tránh đầy cache
                            tmp.file.delete()
                            _uploadProgress.value = done.toFloat() / totalFiles.toFloat()
                        }
                        MultipartBody.Part.createFormData("images", tmp.file.name, body)
                    }
                }

                // 4) Gọi API — tiến trình nhảy mỗi khi 1 file xong
                val response = postRepository.createPost(
                    userIdPart, userModelPart, contentPart, imageParts, keywordsPart
                )

                if (response.isSuccessful) {
                    _createPostResponse.value = response.body()
                    _uiStatePost.value = UiState.Success("Đăng bài thành công")
                    withContext(Dispatchers.Main) {
                        // Nếu không có file, đảm bảo vẫn về 100%
                        if (totalFiles == 0) _uploadProgress.value = 1f
                        Toast.makeText(context, "Đăng bài thành công", Toast.LENGTH_SHORT).show()
                    }
                    fetchPosts()
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    _uiStatePost.value = UiState.Error("Đăng bài thất bại: $errorBody")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Đăng bài thất bại: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                    println("Đăng bài thất bại: $errorBody")
                }
            } catch (e: Exception) {
                _uiStatePost.value = UiState.Error("Lỗi: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                println("Lỗi: ${e.message}")
            } finally {
                // Cho người dùng thấy 100% một nhịp rồi reset (tùy bạn):
                delay(2000)
                _uploadProgress.value = 0f
                _uiStatePost.value = UiState.Idle
            }
        }
    }

    private fun prepareFilePart(
        context: Context,
        fileUri: Uri,
        partName: String
    ): MultipartBody.Part? {
        return try {
            val mimeType = context.contentResolver.getType(fileUri) ?: "application/octet-stream"

            // Tạo file tạm (không cần tên gốc)
            val tempFile = File.createTempFile("upload_", null, context.cacheDir)

            context.contentResolver.openInputStream(fileUri)?.use { input ->
                FileOutputStream(tempFile).use { output -> input.copyTo(output) }
            } ?: run {
                Log.e("PostViewModel", "Không mở được InputStream cho $fileUri")
                return null
            }

            val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
        } catch (e: Exception) {
            Log.e("PostViewModel", "Error preparing file part: $fileUri", e)
            null
        }
    }
    /** RequestBody gọi onFileUploaded() đúng 1 lần khi file này đã ghi xong vào request. */
    class FileCompletionRequestBody(
        private val file: File,
        private val mediaType: MediaType?,
        private val onFileUploaded: () -> Unit
    ) : RequestBody() {

        private val done = AtomicBoolean(false)

        override fun contentType(): MediaType? = mediaType
        override fun contentLength(): Long = file.length()

        override fun writeTo(sink: BufferedSink) {
            file.source().use { source ->
                sink.writeAll(source)
            }
            if (done.compareAndSet(false, true)) {
                onFileUploaded()
            }
        }
    }

    private val _isFavoritedMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isFavoritedMap: StateFlow<Map<String, Boolean>> = _isFavoritedMap

    private val _totalFavoritesMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val totalFavoritesMap: StateFlow<Map<String, String>> get() = _totalFavoritesMap

    fun fetchFavoriteForPost(postId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = postRepository.getFavoriteByPostId(postId, userId)
                if (response.isSuccessful) {
                    val isFavorited = response.body()?.isFavorited ?: false
                    val totalFavorites = response.body()?.totalFavorites?.toString() ?: "0"
                    println("isFavorited: $isFavorited"+ " totalFavorites: $totalFavorites")
                    _isFavoritedMap.value += (postId to isFavorited)
                    _totalFavoritesMap.value += (postId to totalFavorites)
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetchFavoriteForPost", e)
            }
        }
    }

    fun updateFavoriteForPost(postId: String, userFavouriteId: String, userFavouriteModel: String) {
        viewModelScope.launch {
            try {
                println("updateFavoriteForPost: $postId, $userFavouriteId, $userFavouriteModel")
                val response = postRepository.updateFavoriteByPostId(postId, UpdateFavoritePostRequest(userFavouriteId, userFavouriteModel))
                if (response.isSuccessful) {
                    println("updateFavoriteForPost thanh cong")
                    val isFavorited = response.body()?.isFavorited ?: false
                    val totalFavorites = response.body()?.totalFavorites?.toString() ?: "0"
                    _isFavoritedMap.value += (postId to isFavorited)
                    _totalFavoritesMap.value += (postId to totalFavorites)
                    println("Cap nhat favourite thanh cong ")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error updateFavoriteForPost", e)
            }
        }
    }

    fun sendComment(postId: String, userId: String, userModel: String, content: String) {
        viewModelScope.launch {
            try {
                print("DATA truyen tu send comment $userId, $userModel, $content")
                Log.d("sendComment", "➡ Gửi comment với postId=$postId, userId=$userId, userModel=$userModel, content=$content")

                val response = postRepository.createCommentByPostId(
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
                postRepository.updateCommentById(
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
                val response = postRepository.getCommentByUserId(userId)
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
                val response = postRepository.deleteCommentById(commentId)
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
                val response = postRepository.getUserFavoritePost(userId)
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
                val response = postRepository.deletePostById(postId)
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

                val contentPart = request.content?.let {
                    MultipartBody.Part.createFormData("content", it)
                }

                // Sửa lại phần xử lý media cũ
                val mediaParts = request.media?.mapIndexed { index, url ->
                    MultipartBody.Part.createFormData("media[$index]", url)
                } ?: emptyList()

                val imageParts = request.images?.mapNotNull { uri ->
                    try {
                        prepareFilePart(context, uri, "images")
                    } catch (e: Exception) {
                        Log.e("PostViewModel", "Skipping invalid URI: $uri", e)
                        null
                    }
                } ?: emptyList()

                val response = postRepository.updatePost(
                    postId = postId,
                    content = contentPart,
                    media = mediaParts,
                    images = imageParts
                )

                if (response.isSuccessful) {
                    _updateSuccess.value = true
                    getPostById(postId, context)
                } else {
                    Log.e("PostViewModel", "Update error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Update exception", e)
            } finally {
                _isUpdating.value = false
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

    private val _similarPosts = MutableStateFlow<List<PostResponse>>(emptyList())
    val similarPosts: StateFlow<List<PostResponse>> get() = _similarPosts

    fun getSimilarPosts(postId: String) {
        viewModelScope.launch {
            try {
                val response = postRepository.getSimilarPosts(postId, 10, 0.6)
                println("Get similar posts: "+response.body())
                if (response.isSuccessful) {
                    val similarPostsResponse = response.body() ?: emptyList()
                    _similarPosts.value = similarPostsResponse.map { it.post }  // chỉ lấy post
                } else {
                    Log.e("PostViewModel", "Get Similar Posts failed: ${response.errorBody()?.string()}")
                }
            }
            catch (e: Exception) {
                Log.e("PostViewModel", "Get Similar Posts Error", e)
            }
        }
    }

    fun closeAllPostMenus() {
        _activePostMenuId.value = null
    }

    fun clearPosts(){
        _posts.value = emptyList()
    }


}