package com.serah.coparenting.room

import androidx.room.*
import com.serah.coparenting.retrofit.User2

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = User2::class,
            parentColumns = ["userId"],
            childColumns = ["senderId"]
        ),
        ForeignKey(
            entity = User2::class,
            parentColumns = ["userId"],
            childColumns = ["recipientId"]
        )
    ]
)
class MessageToSave(
    @PrimaryKey var id: String,
    var title: String,
    var body: String,
    var image: String?,
    @ColumnInfo(name = "senderId") var senderId: String,
    @ColumnInfo(name = "recipientId") var recipientId: String,
    var createdAt: String
) {
    @ColumnInfo(name = "sender")
    @Ignore
    var sender: User2? = null

    @ColumnInfo(name = "recipient")
    @Ignore
    var recipient: User2? = null

}
