package com.example.consumeradda.service

import com.example.consumeradda.models.authModels.Register
import com.example.consumeradda.models.authModels.RegisterDefaultResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {

    @POST("createUser")
    fun registerUser(@Body userData: Register): Call<RegisterDefaultResponse>

    @GET("userData/{id}")
    fun getUserData(@Path ("id") id:String): Call<List<Register>>
}