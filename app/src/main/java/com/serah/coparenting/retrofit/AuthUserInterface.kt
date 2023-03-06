package com.serah.coparenting.retrofit

interface AuthUserInterface {
    fun success(user:AuthUser)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}