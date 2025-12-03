package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.NewsService
import com.hellodoc.healthcaresystem.requestmodel.CreateNewsCommentRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateNewsFavoriteRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsService: NewsService
) {
    suspend fun getAllNews() = newsService.getAllNews()
    suspend fun createNews(
        adminId: RequestBody,
        title: RequestBody,
        content: RequestBody,
        images: List<MultipartBody.Part>
    ) = newsService.createNews(
        adminId, title, content, images
    )

    suspend fun deleteNews(newsId: String) = newsService.deleteNews(newsId)
//    suspend fun updateNews(newsId: String, newsRequest: NewsRequest) = newsService.updateNews(newsId, newsRequest)
//    suspend fun getNewsById(newsId: String) = newsService.getNewsById(newsId)
//    suspend fun getComments(newsId: String) = newsService.getComments(newsId)
//    suspend fun createComment(newsId: String, commentRequest: CommentRequest) = newsService.createComment(newsId, commentRequest)
//    suspend fun updateComment(
//        commentId: String,
//        commentRequest: CreateNewsCommentRequest
//    ) = newsService.updateCommentById(
//        commentId, commentRequest
//    )
//    suspend fun deleteComment(commentId: String) = newsService.deleteCommentById(commentId)

    suspend fun getFavoriteByNewsId(
        newsId: String,
        userId: String
    ) = newsService.getFavoriteByNewsId(newsId, userId)

    suspend fun updateFavoriteByNewsId(
        newsId: String,
        request: UpdateNewsFavoriteRequest
    ) = newsService.updateFavoriteByNewsId(
        newsId, request
    )

    suspend fun getCommentByNewsId(
        newsId: String,
    ) = newsService.getCommentByNewsId(
        newsId
    )

    suspend fun createCommentByNewsId(
        newsId: String,
        createNewsCommentRequest: CreateNewsCommentRequest
    ) = newsService.createCommentByNewsId(
        newsId, createNewsCommentRequest
    )

    suspend fun updateCommentById(
        commentId: String,
        update: CreateNewsCommentRequest
    ) = newsService.updateCommentById(
        commentId, update
    )

    suspend fun deleteCommentById(
        commentId: String
    ) = newsService.deleteCommentById(
        commentId
    )

}