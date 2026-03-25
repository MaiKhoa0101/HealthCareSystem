package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.GestureCodeService
import com.hellodoc.healthcaresystem.model.api.PostService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.CreateCommentPostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.CreatePostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureCodeResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetCommentPageResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetFavoritePostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetPostPageResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ManagerResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.SimilarPostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UpdateFavoritePostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordCode
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.urlMedia
import com.hellodoc.healthcaresystem.requestmodel.CreateCommentPostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateFavoritePostRequest
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import jakarta.inject.Inject
import okhttp3.MultipartBody
import retrofit2.Response

interface PostRepository {
    suspend fun getAllPosts(skip: Int, limit: Int): Response<GetPostPageResponse>
    suspend fun getPostById(id: String): Response<PostResponse>
    suspend fun getPostByUserId(userId: String, skip: Int, limit: Int): Response<GetPostPageResponse>

    suspend fun createPost(
        userId: MultipartBody.Part,
        userModel: MultipartBody
        .Part,
        content: MultipartBody.Part,
        media: List<MultipartBody.Part>,

        keywords: MultipartBody.Part?
    ): Response<CreatePostResponse>

    suspend fun updatePost(
        postId:String,
        content: MultipartBody.Part?,
        media: List<MultipartBody.Part>,
        images: List<MultipartBody.Part>,
        ): Response<Unit>

    suspend fun deletePostById(postId: String): Response<Unit>

    suspend fun getCommentByPostId(
        postId: String,
        skip: Int,
        limit: Int
    ): Response<GetCommentPageResponse>
    suspend fun createEmbedding(
        id:String,
        keywords: String
    ):Response<Void>
    suspend fun addKeywords(
        postId: String,
        request: PostViewModel.UpdateKeywordsRequest
    ):Response<Void>
    suspend fun getFavoriteByPostId(
        postId: String,
        userId: String
    ):Response<GetFavoritePostResponse>

    suspend fun updateFavoriteByPostId(
        postId: String,
        request: UpdateFavoritePostRequest,
        ):Response<UpdateFavoritePostResponse>

    suspend fun createCommentByPostId(
        postId: String,
        request: CreateCommentPostRequest
    ):Response<CreateCommentPostResponse>

    suspend fun updateCommentById(
        commentId: String,
        request: CreateCommentPostRequest
    ):Response<Unit>

    suspend fun getCommentByUserId(
        userId: String
    ):Response<List<ManagerResponse>>

    suspend fun deleteCommentById(
        commentId: String
    ): Response<Unit>

    suspend fun getUserFavoritePost(
        userId: String,):Response<List<ManagerResponse>>

    suspend fun getSimilarPosts(
        postId: String,
        limit: Int,
        minSimilarity: Double):Response<List<SimilarPostResponse>>

    suspend fun searchAdvanced(
        keyword: String
    ):Response<List<PostResponse>>

    suspend fun getGestureCode(urlMedia: String): Response<List<GestureCodeResponse>>
    suspend fun postVideoToGetGestureCode(urlMedia: String):Response<List<GestureCodeResponse>>
}


class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val gestureCodeService: GestureCodeService
):PostRepository{
    override suspend fun getAllPosts(skip: Int ,limit: Int) = postService.getAllPosts(skip, limit)
    override suspend fun getPostById(id: String) = postService.getPostById(id)
    override suspend fun getPostByUserId(userId: String, skip: Int, limit: Int)
            = postService.getPostByUserId(userId, skip, limit)
    override suspend fun createPost(
        userId: MultipartBody.Part,
        userModel: MultipartBody.Part,
        content: MultipartBody.Part,
        media: List<MultipartBody.Part>,
        keywords: MultipartBody.Part?
    ) = postService.createPost(
        userId = userId,
        userModel = userModel,
        content = content,
        images = media,
        keywordsPart = keywords
    )
    override suspend fun updatePost(
        postId:String,
        content: MultipartBody.Part?,
        media: List<MultipartBody.Part>,
        images: List<MultipartBody.Part>
    ) = postService.updatePost(
        postId = postId,
        content = content,
        media = media,
        images = images
    )
    override suspend fun deletePostById(postId: String) = postService.deletePostById(postId)

    override suspend fun getCommentByPostId(
        postId: String,
        skip: Int,
        limit: Int
    ) = postService.getCommentByPostId(postId, skip, limit)

    override suspend fun createEmbedding(
        id:String,
        keywords: String
    ) = postService.createEmbedding(id, keywords)

    override suspend fun addKeywords(
        postId: String,
        request: PostViewModel.UpdateKeywordsRequest
    ) = postService.addKeywords(postId, request)

    override suspend fun getFavoriteByPostId(
        postId: String,
        userId: String
    ) = postService.getFavoriteByPostId(postId, userId)

    override suspend fun updateFavoriteByPostId(
        postId: String,
        request: UpdateFavoritePostRequest
    ) = postService.updateFavoriteByPostId(postId, request)

    override suspend fun createCommentByPostId(
        postId: String,
        request: CreateCommentPostRequest
    ) = postService.createCommentByPostId(postId, request)

    override suspend fun updateCommentById(
        commentId: String,
        request: CreateCommentPostRequest
    ) = postService.updateCommentById(commentId, request)

    override suspend fun getCommentByUserId(
        userId: String
    ) = postService.getCommentByUserId(userId)

    override suspend fun deleteCommentById(
        commentId: String
    ) = postService.deleteCommentById(commentId)

    override suspend fun getUserFavoritePost(
        userId: String
    ) = postService.getUserFavoritePost(userId)

    override suspend fun getSimilarPosts(
        postId: String,
        limit: Int,
        minSimilarity: Double
    ) = postService.getSimilarPosts(
        postId = postId,
        limit = limit,
        minSimilarity = minSimilarity
    )

    override suspend fun searchAdvanced(
        keyword: String
    ) = postService.searchAdvanced(keyword)

    override suspend fun getGestureCode(urlMedia:String) = gestureCodeService.getGestureCode(urlMedia)
    override suspend fun postVideoToGetGestureCode(urlMedia: String) = gestureCodeService.postVideoToGetGestureCode(
        urlMedia(urlMedia)
    )
}