package com.example.consumeradda.service

import com.example.consumeradda.models.caseModels.*
import retrofit2.Call
import retrofit2.http.*

interface CaseService {

    @GET("getCases/{id}/{token}")
    fun fetchCases(@Path("id") id:String, @Path("token") token:String): Call<MutableList<Case>>

    @PATCH("addcase/{token}")
    fun addCase(@Path("token") token:String) : Call<CaseNumberResponse>

    @PATCH("accept/{id}/{token}")
    fun acceptCase(@Path("id") id:Int, @Path("token") token:String ,@Body lawyerData: AcceptCase) : Call<AcceptCaseResponse>

    @GET("/seeCasesS/{id}/{token}")
    fun seeStateCases(@Path("id") id:Int, @Path("token") token:String): Call<MutableList<Case>>

    @GET("seeCasesSD/{state}/{district}/{token}")
    fun seeStateDistrictCases(@Path("state") state:Int, @Path("district") district:Int,@Path("token") token:String): Call<MutableList<Case>>

    @POST("submitCase/{token}")
    fun submitCase(@Path("token") token:String, @Body caseData : CaseData): Call<CaseResponse>

    @PATCH("/updateStatus/{id}/{status}/{token}")
    fun updateCaseStatus(@Path("id") id:Int,@Path("status") status:Int,@Path("token") token:String): Call<CaseResponse>
}