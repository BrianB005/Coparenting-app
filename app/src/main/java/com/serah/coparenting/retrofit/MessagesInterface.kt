package com.serah.coparenting.retrofit

interface MessagesInterface {
    fun success(messages:List<Message>)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}