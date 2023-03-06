package com.serah.coparenting.room

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.serah.coparenting.retrofit.Message
import com.serah.coparenting.retrofit.User2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RoomViewModel(application: Application): AndroidViewModel(application) {
    private val repository:MessageRepo;
    private val userRepository:UserRepo;
    private val user:LiveData<User2>
    val allMessages: LiveData<List<MessageRetrieved>>;


    init{
        val messageDao=MyDatabase.getInstance(application).messageDao()
        repository= MessageRepo(messageDao)

        allMessages=repository.allMessages

        val userDao=MyDatabase.getInstance(application).userDao()
        userRepository= UserRepo(userDao)

        user=userRepository.user
    }
    fun addMessage(message: MessageToSave){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMessage(message)
        }
    }
    fun addMessages(vararg messages: MessageToSave){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMessages(*messages)
        }
    }


    fun addUser(user:User2){
        viewModelScope.launch (Dispatchers.IO){
            userRepository.addUser(user)
        }
    }
}