package com.example.healthcaresystem.api

import com.example.healthcaresystem.responsemodel.GetFAQItemResponse
import com.example.healthcaresystem.responsemodel.GetSpecialtyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface FAQItem {
    @Headers("Content-Type: application/json")
    @GET("faqitem/get-all")
    suspend fun getFAQItems(): Response<List<GetFAQItemResponse>>
}