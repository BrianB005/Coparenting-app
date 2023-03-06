package com.serah.coparenting.retrofit

import okhttp3.ResponseBody

interface CreateEventInterface {
    fun success(event:ResponseBody)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}