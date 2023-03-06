package com.serah.coparenting.retrofit

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
class User2(@SerializedName("_id") @PrimaryKey  val userId:String,
           val firstName:String, val lastName:String,val email:String,val phone:String, val coparent: String,val profilePic:String?,
           val createdAt:String
) {
}