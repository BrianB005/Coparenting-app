package com.serah.coparenting.room

import android.util.Log
import androidx.lifecycle.LiveData
import com.serah.coparenting.retrofit.Message

class MessageRepo(private val messageDao: MessageDao) {
    val allMessages: LiveData<List<MessageRetrieved>> = messageDao.getMessagesWithUsers()
    fun addMessage(message:MessageToSave){
        messageDao.insert(message)

    }
    fun addMessages( vararg messages:MessageToSave){
        messageDao.insertMany(*messages)

    }
}