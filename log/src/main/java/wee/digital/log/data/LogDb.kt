package wee.digital.log.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import wee.digital.log.BuildConfig
import wee.digital.log.LogBook

@Database(
        entities = [CrashItem::class, EnrollItem::class],
        version = 2,
        exportSchema = false
)
abstract class LogDb : RoomDatabase() {

    abstract val crashDao: CrashItem.DAO

    abstract val enrollDao: EnrollItem.DAO

    companion object {

        val instance: LogDb by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(LogBook.app.applicationContext, LogDb::class.java, BuildConfig.LIBRARY_PACKAGE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
        }

    }

}