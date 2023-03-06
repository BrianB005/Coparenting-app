package com.serah.coparenting.retrofit

import okhttp3.ResponseBody

interface ExpenseInterface {
    fun success(expense: ResponseBody)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}