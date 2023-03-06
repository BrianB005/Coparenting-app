package com.serah.coparenting.room

import androidx.lifecycle.LiveData
import com.serah.coparenting.retrofit.Message
import com.serah.coparenting.retrofit.User2

class UserRepo(private val userDao: UserDao) {

    val user:LiveData<User2> =userDao.getUser()
    suspend fun addUser(user:User2){userDao.insert(user)}
}