package com.serah.coparenting.retrofit

interface MessageInterface {
    fun success(message:SentMessage)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}