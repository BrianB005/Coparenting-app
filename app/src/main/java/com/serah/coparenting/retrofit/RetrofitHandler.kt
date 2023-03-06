package com.serah.coparenting.retrofit

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.serah.coparenting.MyPreferences
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

import java.io.IOException

class RetrofitHandler {



    companion object {

//        https://coparenting-app.herokuapp.com
        private val  retrofit:Retrofit=Retrofit.Builder()
            .baseUrl("https://coparenting-app.herokuapp.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val myApi: MyApi =retrofit.create(MyApi::class.java)
        fun register(userDetails:HashMap<String,String>,userInterface: AuthUserInterface) {
            val userCall: Call<AuthUser> = myApi.register(userDetails);
            userCall.enqueue(object : Callback<AuthUser> {
                override fun onResponse(call: Call<AuthUser>, response: Response<AuthUser>) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            userInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        userInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<AuthUser>, t: Throwable) {
                    userInterface.failure(t)
                }
            })
        }
        fun login(userDetails: HashMap<String, String>, userInterface: AuthUserInterface) {
            val userCall: Call<AuthUser> = myApi.login(userDetails);
            userCall.enqueue(object : Callback<AuthUser> {
                override fun onResponse(call: Call<AuthUser>, response: Response<AuthUser>) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            userInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        userInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<AuthUser>, t: Throwable) {
                    userInterface.failure(t)
                }
            })


        }
        fun getCurrentUser(context:Context,userInterface:UserInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val userCall= myApi.currentUser("Bearer $token")
            userCall.enqueue(object:Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            userInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {

                        userInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    userInterface.failure(t)
                }

            })

        }
        fun sendMessage(context: Context, message:HashMap<String,String>, messageInterface: MessageInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val messageCall= myApi.sendMessage("Bearer $token",message)
            messageCall.enqueue(object:Callback<SentMessage>{
                override fun onResponse(call: Call<SentMessage>, response: Response<SentMessage>) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            messageInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        messageInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<SentMessage>, t: Throwable) {
                    messageInterface.failure(t)
                }

            })
        }
        fun getMessages(context: Context,messagesInterface: MessagesInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val messagesCall= myApi.getMessages("Bearer $token")
            messagesCall.enqueue(object:Callback<List<Message>>{
                override fun onResponse(
                    call: Call<List<Message>>,
                    response: Response<List<Message>>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            messagesInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        messagesInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                    messagesInterface.failure(t)
                }
            })
        }
        fun getEvents(context: Context,eventsInterface: EventsInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val eventsCall= myApi.getMyEvents("Bearer $token")
            eventsCall.enqueue(object:Callback<List<Event>>{
                override fun onResponse(
                    call: Call<List<Event>>,
                    response: Response<List<Event>>
                ) {
                    if (!response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            eventsInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        eventsInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    eventsInterface.failure(t)
                }
            })
        }
        fun getExpenses(context: Context,expensesInterface: ExpensesInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val eventsCall= myApi.getExpenses("Bearer $token")
            eventsCall.enqueue(object:Callback<List<Expense>>{
                override fun onResponse(
                    call: Call<List<Expense>>,
                    response: Response<List<Expense>>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            expensesInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        expensesInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<Expense>>, t: Throwable) {
                    expensesInterface.failure(t)
                }
            })
        }
        fun createEvent(context: Context,event:CreatedEvent,eventInterface: CreateEventInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val expenseCall= myApi.createEvent("Bearer $token",event)
            expenseCall.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            eventInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        eventInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    eventInterface.failure(t)
                }
            })
        }
        fun createExpense(context: Context,expense:HashMap<String,Any>,expenseInterface: ExpenseInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val eventCall= myApi.createExpense("Bearer $token",expense)
            eventCall.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            expenseInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        expenseInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    expenseInterface.failure(t)
                }
            })
        }

        fun updateEvent(context: Context,id:String,event:HashMap<String,Any>,eventInterface: CreateEventInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val eventCall= myApi.updateEvent("Bearer $token",id,event)
            eventCall.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            eventInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        eventInterface.success(response.body()!!)
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    eventInterface.failure(t)
                }
            })
        }
        fun updateExpense(context: Context,id:String,expense:HashMap<String,Any>,expenseInterface: ExpenseInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val eventCall= myApi.updateExpense("Bearer $token",id,expense)
            eventCall.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            expenseInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        expenseInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    expenseInterface.failure(t)
                }
            })
        }
        fun addCoparent(context: Context,userId:String,responseBodyInterface: ResponseBodyInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val responseCall= myApi.addCoparent("Bearer $token",userId)
            responseCall.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            responseBodyInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        responseBodyInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    responseBodyInterface.failure(t)
                }
            })
        }
        fun createGallery(context: Context,gallery:HashMap<String,Any>,responseBodyInterface: ResponseBodyInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val responseCall= myApi.createGallery("Bearer $token",gallery)
            responseCall.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            responseBodyInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        responseBodyInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    responseBodyInterface.failure(t)
                }
            })
        }


        fun getGallery(context: Context,galleryInterface: GalleryInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val galleryCall= myApi.getGallery("Bearer $token")
            galleryCall.enqueue(object:Callback<List<Gallery>>{
                override fun onResponse(
                    call: Call<List<Gallery>>,
                    response: Response<List<Gallery>>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            galleryInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        galleryInterface.success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<Gallery>>, t: Throwable) {
                    galleryInterface.failure(t)
                }
            })
        }

        fun sendEmail(context: Context,recipient:String,responseBodyInterface: ResponseBodyInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val responseCall= myApi.sendEmail("Bearer $token",recipient)
            responseCall.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        try {

                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            responseBodyInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        responseBodyInterface.success(response.body()!!)
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    responseBodyInterface.failure(t)
                }
            })
        }

        fun updateUser(context: Context,update:HashMap<String,String>,responseBodyInterface: ResponseBodyInterface){
            val token=MyPreferences.getItemFromSP(context,"token")!!
            val responseCall= myApi.updateUser("Bearer $token",update)
            responseCall.enqueue(object:Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        try {
                            Log.d("My_response",response.errorBody()!!.string())
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jsonObject.getString("msg")
                            responseBodyInterface.errorExists(errorMessage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        responseBodyInterface.success(response.body()!!)
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    responseBodyInterface.failure(t)
                }
            })
        }

    }

}