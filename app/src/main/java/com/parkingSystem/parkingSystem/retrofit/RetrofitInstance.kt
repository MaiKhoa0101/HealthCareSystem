package com.parkingSystem.parkingSystem.retrofit

import com.parkingSystem.parkingSystem.api.AdminService
import com.parkingSystem.parkingSystem.api.AppointmentService
import com.parkingSystem.parkingSystem.api.AuthService
import com.parkingSystem.parkingSystem.api.ParkingService
import com.parkingSystem.parkingSystem.api.FAQItemService
import com.parkingSystem.parkingSystem.api.GeminiService
import com.parkingSystem.parkingSystem.api.MedicalOptionService
import com.parkingSystem.parkingSystem.api.NewsService
import com.parkingSystem.parkingSystem.api.NotificationService
import com.parkingSystem.parkingSystem.api.RemoteMedicalOptionService
import com.parkingSystem.parkingSystem.api.ReportService
import com.parkingSystem.parkingSystem.api.ReviewService
import com.parkingSystem.parkingSystem.api.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit 

object RetrofitInstance {
   private const val BASE_URL = "http://192.168.1.132:4000"
   //private const val BASE_URL = "https://healthcare-backend-yc39.onrender.com"
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
    val parking: ParkingService by lazy { retrofit.create(ParkingService::class.java) }
    val appointment: AppointmentService by lazy { retrofit.create(AppointmentService::class.java) }
    val medicalOptionService: MedicalOptionService by lazy { retrofit.create(MedicalOptionService::class.java) }
    val remoteMedicalOptionService: RemoteMedicalOptionService by lazy { retrofit.create(RemoteMedicalOptionService::class.java) }
    val faqItemService: FAQItemService by lazy { retrofit.create(FAQItemService::class.java) }
    val reviewService: ReviewService by lazy { retrofit.create(ReviewService::class.java) }
    val reportService: ReportService by lazy { retrofit.create(ReportService::class.java) }
    val notificationService: NotificationService by lazy { retrofit.create(NotificationService::class.java) }
    val newsService: NewsService by lazy { retrofit.create(NewsService::class.java) }



    val userService: UserService by lazy { retrofit.create(UserService::class.java) }

}
