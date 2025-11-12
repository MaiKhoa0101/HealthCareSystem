package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.PostService
import com.hellodoc.healthcaresystem.requestmodel.CreateCommentPostRequest
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateFavoritePostRequest
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import jakarta.inject.Inject
import okhttp3.MultipartBody

class PostRepository @Inject constructor(private val postService: PostService){
    suspend fun getAllPosts(skip: Int ,limit: Int) = postService.getAllPosts(skip, limit)
    suspend fun getPostById(id: String) = postService.getPostById(id)
    suspend fun getPostByUserId(userId: String, skip: Int, limit: Int)
            = postService.getPostByUserId(userId, skip, limit)
    suspend fun createPost(
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
    suspend fun updatePost(
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
    suspend fun deletePostById(postId: String) = postService.deletePostById(postId)
    suspend fun getCommentByPostId(
        postId: String,
        skip: Int,
        limit: Int
    ) = postService.getCommentByPostId(postId, skip, limit)

    suspend fun createEmbedding(
        id:String,
        keywords: String
    ) = postService.createEmbedding(id, keywords)

    suspend fun addKeywords(
        postId: String,
        request: PostViewModel.UpdateKeywordsRequest
    ) = postService.addKeywords(postId, request)

    suspend fun getFavoriteByPostId(
        postId: String,
        userId: String
    ) = postService.getFavoriteByPostId(postId, userId)

    suspend fun updateFavoriteByPostId(
        postId: String,
        request: UpdateFavoritePostRequest
    ) = postService.updateFavoriteByPostId(postId, request)

    suspend fun createCommentByPostId(
        postId: String,
        request: CreateCommentPostRequest
    ) = postService.createCommentByPostId(postId, request)

    suspend fun updateCommentById(
        commentId: String,
        request: CreateCommentPostRequest
    ) = postService.updateCommentById(commentId, request)

    suspend fun getCommentByUserId(
        userId: String
    ) = postService.getCommentByUserId(userId)

    suspend fun deleteCommentById(
        commentId: String
    ) = postService.deleteCommentById(commentId)

    suspend fun getUserFavoritePost(
        userId: String
    ) = postService.getUserFavoritePost(userId)

    suspend fun getSimilarPosts(
        postId: String,
        limit: Int,
        minSimilarity: Double
    ) = postService.getSimilarPosts(
        postId = postId,
        limit = limit,
        minSimilarity = minSimilarity
    )

    suspend fun searchAdvanced(
        keyword: String
    ) = postService.searchAdvanced(keyword)



}