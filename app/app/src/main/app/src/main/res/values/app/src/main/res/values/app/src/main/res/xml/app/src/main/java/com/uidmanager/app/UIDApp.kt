package com.uidmanager.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uid: String = "",
    val password: String = "",
    val cookies: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@androidx.room.Dao
interface EntryDao {
    @androidx.room.Query("SELECT * FROM entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Entry>>

    @androidx.room.Insert
    suspend fun insert(entry: Entry): Long

    @androidx.room.Update
    suspend fun update(entry: Entry)

    @androidx.room.Query("SELECT * FROM entries WHERE id = :id")
    suspend fun getById(id: Long): Entry?

    @androidx.room.Query("DELETE FROM entries WHERE id = :id")
    suspend fun delete(id: Long)
}

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val key: String,
    val value: String
)

@androidx.room.Dao
interface SettingsDao {
    @androidx.room.Query("SELECT * FROM settings WHERE `key` = :key")
    suspend fun get(key: String): Settings?

    @androidx.room.Insert
    suspend fun insert(settings: Settings)

    @androidx.room.Update
    suspend fun update(settings: Settings)
}

@Database(entities = [Entry::class, Settings::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(ctx: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext, AppDatabase::class.java, "uid_manager.db"
                ).build().also { INSTANCE = it }
            }
    }
}

class UIDApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "overlay_channel",
                "UID Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the floating overlay active"
            }
            val mgr = getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(channel)
        }
    }
}
