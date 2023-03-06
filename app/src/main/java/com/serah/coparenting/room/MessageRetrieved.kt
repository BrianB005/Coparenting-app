package com.serah.coparenting.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.serah.coparenting.retrofit.User2

@Entity(tableName = "messages")
data class MessageRetrieved( @Embedded val message: MessageToSave,
                             @Relation(
                                 parentColumn = "senderId",
                                 entityColumn = "userId"
                             )
                             val sender: User2,
                             @Relation(
                                 parentColumn = "recipientId",
                                 entityColumn = "userId"
                             )
                             val recipient: User2){
}