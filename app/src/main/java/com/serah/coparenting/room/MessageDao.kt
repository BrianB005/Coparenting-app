package com.serah.coparenting.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.serah.coparenting.retrofit.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(message: MessageToSave)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(vararg messages: MessageToSave)

    @Update
    fun update(message: MessageToSave)
    @Delete
    fun delete(message: MessageToSave)
    @Transaction
    @Query("SELECT * FROM messages")
    fun getMessagesWithUsers(): LiveData<List<MessageRetrieved>>
}