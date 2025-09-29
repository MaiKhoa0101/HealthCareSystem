package com.hellodoc.healthcaresystem.retrofit

import com.hellodoc.healthcaresystem.api.AdminService
import com.hellodoc.healthcaresystem.api.AppointmentService
import com.hellodoc.healthcaresystem.api.AuthService
import com.hellodoc.healthcaresystem.api.DoctorService
import com.hellodoc.healthcaresystem.api.FAQItemService
import com.hellodoc.healthcaresystem.api.GeminiService
import com.hellodoc.healthcaresystem.api.MedicalOptionService
import com.hellodoc.healthcaresystem.api.NewsService
import com.hellodoc.healthcaresystem.api.NotificationService
import com.hellodoc.healthcaresystem.api.PostService
import com.hellodoc.healthcaresystem.api.RemoteMedicalOptionService
import com.hellodoc.healthcaresystem.api.ReportService
import com.hellodoc.healthcaresystem.api.ReviewService
import com.hellodoc.healthcaresystem.api.SpecialtyService
import com.hellodoc.healthcaresystem.api.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit 

object RetrofitInstance {
//    private const val BASE_URL = "http://192.168.1.217:4000"
   private const val BASE_URL = "https://healthcare-backend-yc39.onrender.com"
//    private const val BASE_URL = "http://192.168.1.110:4000"
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

    // Các service API
    val api: AuthService by lazy { retrofit.create(AuthService::class.java) }
    val admin: AdminService by lazy { retrofit.create(AdminService::class.java) }
    val doctor: DoctorService by lazy { retrofit.create(DoctorService::class.java) }
    val appointment: AppointmentService by lazy { retrofit.create(AppointmentService::class.java) }
    val specialtyService: SpecialtyService by lazy { retrofit.create(SpecialtyService::class.java) }
    val medicalOptionService: MedicalOptionService by lazy { retrofit.create(MedicalOptionService::class.java) }
    val remoteMedicalOptionService: RemoteMedicalOptionService by lazy { retrofit.create(RemoteMedicalOptionService::class.java) }
    val faqItemService: FAQItemService by lazy { retrofit.create(FAQItemService::class.java) }
    val postService: PostService by lazy { retrofit.create(PostService::class.java) }
    val reviewService: ReviewService by lazy { retrofit.create(ReviewService::class.java) }
    val reportService: ReportService by lazy { retrofit.create(ReportService::class.java) }
    val notificationService: NotificationService by lazy { retrofit.create(NotificationService::class.java) }
    val newsService: NewsService by lazy { retrofit.create(NewsService::class.java) }



    val userService: UserService by lazy { retrofit.create(UserService::class.java) }
    val geminiService: GeminiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService::class.java)
    }
}
