package com.example.consumeradda.models.caseModels

data class Case (
        var lawyerFirebaseId:String,
        var lawyerName:String,
        var lawyerEmail:String,
        var _id:String,
        var caseId:Int,
        var applicantFirebaseId:String,
        var applicantEmail:String,
        var applicantFirstName:String,
        var applicantLastName:String,
        var applicantState:String,
        var applicantDistrict:String,
        var caseAgainst:String,
        var caseType:String,
        var caseDescription:String,
        var caseMoney:String,
        var attachment1:String,
        var attachment2: String,
        var attachment3: String,
        var attachment4: String,
        var attachment5: String,
        var applicantPhone:String,
        var caseStatus:Int,
        var lastActionDate:String,
        var __v:Int
)