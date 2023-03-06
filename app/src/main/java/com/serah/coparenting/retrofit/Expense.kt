package com.serah.coparenting.retrofit

import com.google.gson.annotations.SerializedName

class Expense(@SerializedName("_id")val id:String,val user:User2,val title:String,val paid:Array<String>,val createdAt:String,val amount:Int,val category:String,val isAccepted:Boolean,val rejectionMessage:String?) {
}