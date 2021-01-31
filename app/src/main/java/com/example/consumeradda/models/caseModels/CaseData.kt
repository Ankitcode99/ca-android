package com.example.consumeradda.models.caseModels

data class CaseData(
        var caseId:Int,
        var applicantFirebaseId:String,
        var applicantEmail:String,
        var applicantFirstName:String,
        var applicantLastName:String,
        var applicantState:String,
        var stateNumber:Int,
        var applicantDistrict:String,
        var districtNumber:Int,
        var caseAgainst:String,
        var caseType:String,
        var caseDescription:String,
        var caseMoney:String,
        var attachment1:String,
        var attachment2:String,
        var attachment3:String,
        var attachment4:String,
        var attachment5:String,
        var applicantPhone:String,
        var lawyerFirebaseId:String,
        var lawyerName:String,
        var lawyerEmail:String,
        var caseStatus: Int
)