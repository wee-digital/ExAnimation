package wee.digital.log.data

import androidx.room.*

@Entity(tableName = "crashes")
class CrashItem(

        @PrimaryKey
        @ColumnInfo(name = "crash_id")
        var id: Int? = null,

        @ColumnInfo(name = "crash_time")
        var time: Long = System.currentTimeMillis(),

        @ColumnInfo(name = "crash_title")
        var title: String,

        @ColumnInfo(name = "crash_trace")
        var trace: String,

        @ColumnInfo(name = "crash_cause")
        var cause: String
) {
    @Dao
    interface DAO {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(t: CrashItem)

        @Query("SELECT * FROM crashes")
        fun all(): List<CrashItem>

        @Query("DELETE FROM crashes")
        fun clear()
    }
}