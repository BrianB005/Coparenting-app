package com.serah.coparenting.retrofit

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitHandler {



    companion object {

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
    }

}