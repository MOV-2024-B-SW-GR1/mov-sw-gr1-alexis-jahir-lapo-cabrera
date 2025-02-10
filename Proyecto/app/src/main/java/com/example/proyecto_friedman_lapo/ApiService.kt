package com.example.proyecto_friedman_lapo

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer "
    )
    @POST("v1/chat/completions")
    suspend fun getChatResponse(@Body request: ChatRequest): Response<ChatResponse>
}
