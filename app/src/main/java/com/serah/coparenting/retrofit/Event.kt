package com.serah.coparenting.retrofit

import com.google.gson.annotations.SerializedName


class Event(@SerializedName("_id")val id:String ,val startDate:String, val endDate: String, val user: User2, val isAccepted:Boolean, val rejectionMessage:String?, val createdAt:String, val title:String ) {
}