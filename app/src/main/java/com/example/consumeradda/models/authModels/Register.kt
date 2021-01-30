package com.example.consumeradda.models.authModels

data class Register (
    val name:String,
    val email:String,
    val firebaseId:String,
    val phoneNumber:String,
    val isLawyer:Boolean,
    var isVerified:Boolean
)
