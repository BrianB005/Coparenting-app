package com.serah.coparenting.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.serah.coparenting.retrofit.Message
import com.serah.coparenting.retrofit.User2

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User2)
    @Update
    fun update(user: User2)
    @Delete
    fun delete(user: User2)
    @Transaction
    @Query("select * from users order by createdAt asc limit 1")
    fun getUser(): LiveData<User2>
}