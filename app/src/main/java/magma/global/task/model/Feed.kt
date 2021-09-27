package magma.global.task.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Feed")
class Feed(


    @ColumnInfo(name = "title")
    var title: String?,

    @PrimaryKey
    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "url")
    var imageUrl: String?,

    @ColumnInfo(name = "deleted")
    var deleted: Boolean,

    @ColumnInfo(name = "deletedDate")
   var deletedDate: Long?

)
