package wee.digital.fpa.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import wee.digital.fpa.app
import wee.digital.fpa.appId
import wee.digital.fpa.data.db.ImageDBO
import wee.digital.fpa.roomVersion

val room: RoomDB by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    Room.databaseBuilder(app.applicationContext, RoomDB::class.java, appId)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
}

@Database(
        entities = [ImageDBO::class],
        version = roomVersion,
        exportSchema = false
)
abstract class RoomDB : RoomDatabase() {

    abstract val imageDao: ImageDBO.DAO
}