package com.serah.coparenting.retrofit


class Message(var _id:String,
    var title:String, var body:String, var image:String?,
    var sender:User2, var recipient:User2, var createdAt:String) {

}