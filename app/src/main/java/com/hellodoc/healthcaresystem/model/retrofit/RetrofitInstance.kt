package com.hellodoc.healthcaresystem.model.retrofit

import com.hellodoc.healthcaresystem.model.api.AdminService
import com.hellodoc.healthcaresystem.api.AppointmentService
import com.hellodoc.healthcaresystem.model.api.AuthService
import com.hellodoc.healthcaresystem.api.DoctorService
import com.hellodoc.healthcaresystem.api.FAQItemService
import com.hellodoc.healthcaresystem.api.GeminiService
import com.hellodoc.healthcaresystem.api.MedicalOptionService
import com.hellodoc.healthcaresystem.api.NewsService
import com.hellodoc.healthcaresystem.api.NotificationService
import com.hellodoc.healthcaresystem.model.api.PostService
import com.hellodoc.healthcaresystem.api.ReportService
import com.hellodoc.healthcaresystem.api.ReviewService
import com.hellodoc.healthcaresystem.model.api.FastTalkService
import com.hellodoc.healthcaresystem.model.api.SpecialtyService
import com.hellodoc.healthcaresystem.model.api.SubtitleService
import com.hellodoc.healthcaresystem.model.api.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {
//   private const val BASE_URL = "http://192.168.1.225:4000"
//   private const val BASE_URL = "https://healthcare-backend-yc39.onrender.com"
    const val BASE_URL = "http://192.168.1.117:4000"


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)  // Thời gian timeout kết nối
        .writeTimeout(60, TimeUnit.SECONDS)    // Thời gian timeout ghi dữ liệuz
        .readTimeout(60, TimeUnit.SECONDS)     // Thời gian timeout đọc dữ liệu
        .build()

    // Tạo instance Retrofit duy nhất
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Dùng gson để chuyển JSON thành obj
            .build()
    }

    val api: AuthService by lazy { retrofit.create(AuthService::class.java) }

    // Cung cấp các API Service
    @Provides fun provideUserService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)
    @Provides fun provideDoctorService(retrofit: Retrofit): DoctorService = retrofit.create(DoctorService::class.java)
    @Provides fun providePostService(retrofit: Retrofit): PostService = retrofit.create(PostService::class.java)
    @Provides fun provideNewsService(retrofit: Retrofit): NewsService = retrofit.create(NewsService::class.java)
    @Provides fun provideMedicalOptionService(retrofit: Retrofit): MedicalOptionService = retrofit.create(MedicalOptionService::class.java)
    @Provides fun provideSpecialtyService(retrofit: Retrofit): SpecialtyService = retrofit.create(SpecialtyService::class.java)
    @Provides fun provideFAQItemService(retrofit: Retrofit): FAQItemService = retrofit.create(FAQItemService::class.java)
    @Provides fun provideAppointmentService(retrofit: Retrofit): AppointmentService = retrofit.create(AppointmentService::class.java)
    @Provides fun provideNotificationService(retrofit: Retrofit): NotificationService = retrofit.create(NotificationService::class.java)
    @Provides fun provideReportService(retrofit: Retrofit): ReportService = retrofit.create(ReportService::class.java)
    @Provides fun provideReviewService(retrofit: Retrofit): ReviewService = retrofit.create(ReviewService::class.java)
    @Provides fun provideAdminService(retrofit: Retrofit): AdminService = retrofit.create(AdminService::class.java)
    @Provides fun provideGeminiService(retrofit: Retrofit): GeminiService = retrofit.create(GeminiService::class.java)
    @Provides fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)
    @Provides fun provideFastTalk(retrofit: Retrofit): FastTalkService = retrofit.create(FastTalkService::class.java)

    @Provides fun provideSubtitleService(retrofit: Retrofit): SubtitleService = retrofit.create( SubtitleService::class.java)

    val geminiService: GeminiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService::class.java)
    }
}
