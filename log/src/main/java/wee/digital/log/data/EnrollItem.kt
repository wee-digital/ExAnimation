package wee.digital.log.data

import androidx.room.*

@Entity(tableName = "enrolls")
class EnrollItem(

        @PrimaryKey
        @ColumnInfo(name = "enroll_id")
        var id: Int? = null,

        @ColumnInfo(name = "enroll_time")
        var detectedTime: Long,

        @ColumnInfo(name = "enroll_name")
        var name: String,

        @ColumnInfo(name = "enroll_request_time")
        var requestedTime: Long,

        @ColumnInfo(name = "enroll_response_time")
        var responseTime: Long,

        @ColumnInfo(name = "enroll_size")
        var requestContentLength: Long
) {

    companion object {
        val now: Long get() = System.currentTimeMillis()
    }

    @Dao
    interface DAO {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(t: EnrollItem)

        @Query("SELECT * FROM enrolls")
        fun all(): List<EnrollItem>

        @Query("DELETE FROM enrolls")
        fun clear()
    }
}