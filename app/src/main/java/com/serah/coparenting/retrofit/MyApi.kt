package com.serah.coparenting.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MyApi {
    @POST("auth/register")
    fun register(@Body userDetails: HashMap<String, String>): Call<AuthUser>

    @POST("auth/login")
    fun login(@Body userDetails: HashMap<String, String>): Call<AuthUser>
}