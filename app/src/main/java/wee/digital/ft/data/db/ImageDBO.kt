package wee.digital.ft.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
class ImageDBO {

    @PrimaryKey
    @ColumnInfo(name = "template_id")
    var id: Int? = null //  id auto increments

    @ColumnInfo(name = "template_image", typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray? = null

    @Dao
    interface DAO : BaseDao<ImageDBO>
}