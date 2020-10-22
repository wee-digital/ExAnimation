package wee.digital.fpa.data

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import wee.digital.fpa.app.appId
import wee.digital.fpa.app.roomVersion
import wee.digital.fpa.data.db.ImageDBO
import wee.digital.library.Library.app

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

private val shared: SharedPreferences by lazy {
    app.getSharedPreferences(appId, Context.MODE_PRIVATE)
}

fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    this.edit().block()
    commit()
}

fun SharedPreferences.clear() {
    this.edit().clear()
    commit()
}

fun SharedPreferences.commit() {
    this.edit().apply()
}
