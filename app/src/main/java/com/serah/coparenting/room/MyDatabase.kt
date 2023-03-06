package com.serah.coparenting.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.serah.coparenting.retrofit.User2



@Database(entities=[MessageToSave::class, User2::class],version=1, exportSchema = false)
abstract class MyDatabase: RoomDatabase() {
    abstract fun messageDao():MessageDao
    abstract fun userDao():UserDao
    companion object {
        @Volatile
        private var databaseInstance:MyDatabase?=null

        fun getInstance(context: Context): MyDatabase {
            var instance = databaseInstance
            if (instance != null) {
                return instance;
            }
            synchronized(this) {
                val newInstance =
                    Room.databaseBuilder(context, MyDatabase::class.java, "my_database").build()
                instance = newInstance
                return newInstance
            }
        }
    }

}