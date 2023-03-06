package com.serah.coparenting.retrofit

interface EventsInterface {
    fun success(events:List<Event>)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}