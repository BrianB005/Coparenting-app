package com.serah.coparenting.retrofit

import com.google.gson.annotations.SerializedName

class User(@SerializedName("_id")  val userId:String,
            val firstName:String, val lastName:String, val password:String,val email:String,val phone:String, val coparent: User?,val profilePic:Image
) {
}