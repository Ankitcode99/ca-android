package com.example.consumeradda.service

import com.example.consumeradda.models.caseModels.*
import retrofit2.Call
import retrofit2.http.*

interface CaseService {

    @GET("getCases/{id}")
    fun fetchCases(@Path("id") id:String): Call<MutableList<Case>>

    @PATCH("addcase")
    fun addCase() : Call<CaseNumberResponse>

    @PATCH("accept/{id}")
    fun acceptCase(@Path("id") id:Int, @Body lawyerData: AcceptCase) : Call<AcceptCaseResponse>

    @GET("/seeCasesS/{id}")
    fun seeStateCases(@Path("id") id:Int): Call<MutableList<Case>>

    @GET("seeCasesSD/{state}/{district}")
    fun seeStateDistrictCases(@Path("state") state:Int, @Path("district") district:Int): Call<MutableList<Case>>

    @POST("submitCase")
    fun submitCase(@Body caseData : CaseData): Call<CaseResponse>
}