package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.NewsService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetFavoritePostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetNewsCommentResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NewsResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UpdateFavoritePostResponse
import com.hellodoc.healthcaresystem.requestmodel.CreateNewsCommentRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateNewsFavoriteRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

interface NewsRepository {
    suspend fun getAllNews(): Response<List<NewsResponse>>
    suspend fun createNews(
        adminId: RequestBody,
        title: RequestBody,
        content: RequestBody,
        images: List<MultipartBody.Part>
    ) : Response<Any>
    suspend fun deleteNews(
        newsId: String
    ): Response<Any>
    suspend fun getFavoriteByNewsId(
        newsId: String,
        userId: String
    ): Response<GetFavoritePostResponse>
    suspend fun updateFavoriteByNewsId(
        newsId: String,
        request: UpdateNewsFavoriteRequest
    ): Response<UpdateFavoritePostResponse>
    suspend fun getCommentByNewsId(
        newsId: String
    ): Response<List<GetNewsCommentResponse>>
    suspend fun createCommentByNewsId(
        newsId: String,
        createNewsCommentRequest: CreateNewsCommentRequest
    ): Response<Unit>
    suspend fun updateCommentById(
        commentId: String,
        update: CreateNewsCommentRequest
    ):Response<Unit>
    suspend fun deleteCommentById(
        commentId: String
    ):Response<Unit>

}

class NewsRepositoryImpl @Inject constructor(
    private val newsService: NewsService
): NewsRepository {
    override suspend fun getAllNews() = newsService.getAllNews()
    override suspend fun createNews(
        adminId: RequestBody,
        title: RequestBody,
        content: RequestBody,
        images: List<MultipartBody.Part>
    ) = newsService.createNews(
        adminId, title, content, images
    )

    override suspend fun deleteNews(newsId: String) = newsService.deleteNews(newsId)
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

    override suspend fun getFavoriteByNewsId(
        newsId: String,
        userId: String
    ) = newsService.getFavoriteByNewsId(newsId, userId)

    override suspend fun updateFavoriteByNewsId(
        newsId: String,
        request: UpdateNewsFavoriteRequest
    ) = newsService.updateFavoriteByNewsId(
        newsId, request
    )

    override suspend fun getCommentByNewsId(
        newsId: String,
    ) = newsService.getCommentByNewsId(
        newsId
    )

    override suspend fun createCommentByNewsId(
        newsId: String,
        createNewsCommentRequest: CreateNewsCommentRequest
    ) = newsService.createCommentByNewsId(
        newsId, createNewsCommentRequest
    )

    override suspend fun updateCommentById(
        commentId: String,
        update: CreateNewsCommentRequest
    ) = newsService.updateCommentById(
        commentId, update
    )

    override suspend fun deleteCommentById(
        commentId: String
    ) = newsService.deleteCommentById(
        commentId
    )

}