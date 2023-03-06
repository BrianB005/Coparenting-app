package com.serah.coparenting.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MyApi {
    @POST("auth/register")
    fun register(@Body userDetails: HashMap<String, String>): Call<AuthUser>
    @POST("auth/login")
    fun login(@Body userDetails: HashMap<String, String>): Call<AuthUser>

    @GET("users/current")
    fun currentUser(@Header("Authorization") token :String ): Call<User>
    @PUT("users/{userId}")
    fun addCoparent(@Header("Authorization")token:String,@Path("userId") userId:String):Call<ResponseBody>
    @PUT("users/current/update")
    fun updateUser(@Header("Authorization")token:String,@Body update:HashMap<String,String>):Call<ResponseBody>


    @POST("messages")
    fun sendMessage(@Header("Authorization") token :String ,@Body message:HashMap<String,String>):Call<SentMessage>
    @GET("messages")
    fun getMessages(@Header("Authorization")token:String):Call<List<Message>>


    @GET("events")
    fun getMyEvents(@Header("Authorization")token:String):Call<List<Event>>
    @POST("events")
    fun createEvent(@Header("Authorization")token:String,@Body event:CreatedEvent):Call<ResponseBody>
    @PUT("events/{id}")
    fun updateEvent(@Header("Authorization")token:String,@Path("id") id:String,@Body event:HashMap<String,Any>):Call<ResponseBody>

    @GET("expenses")
    fun getExpenses(@Header("Authorization")token:String):Call<List<Expense>>
    @POST("expenses")
    fun createExpense(@Header("Authorization")token:String,@Body expense:HashMap<String,Any>):Call<ResponseBody>
    @PUT("expenses/{id}")
    fun updateExpense(@Header("Authorization")token:String,@Path("id") id:String,@Body expense:HashMap<String,Any>):Call<ResponseBody>

    @POST("emails/{recipient}")
    fun sendEmail(@Header("Authorization")token:String,@Path("recipient") id:String):Call<ResponseBody>

    @GET("gallery")
    fun getGallery(@Header("Authorization")token:String):Call<List<Gallery>>
    @POST("gallery")
    fun createGallery(@Header("Authorization")token:String,@Body gallery:HashMap<String,Any>):Call<ResponseBody>






}