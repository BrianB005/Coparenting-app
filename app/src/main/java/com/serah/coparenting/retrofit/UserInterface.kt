package com.serah.coparenting.retrofit

interface UserInterface {
    fun success(user:User)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}