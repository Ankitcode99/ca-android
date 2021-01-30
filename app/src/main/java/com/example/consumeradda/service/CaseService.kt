package com.example.consumeradda.service

import com.example.consumeradda.models.caseModels.Case
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CaseService {

    @GET("getCases/{id}")
    fun fetchCases(@Path("id") id:String): Call<MutableList<Case>>

}