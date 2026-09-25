package com.crudax.launcher.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database foundation.
 * Entities expanded in v2 (AutomationEntity, WidgetEntity, LogEntity…).
 */
@Database(entities = [PlaceholderEntity::class], version = 1, exportSchema = false)
abstract class CrudaxDatabase : RoomDatabase() {
    companion object {
        @Volatile private var INSTANCE: CrudaxDatabase? = null
        fun getInstance(context: Context): CrudaxDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CrudaxDatabase::class.java,
                    "crudax.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}

@Entity(tableName = "placeholder")
data class PlaceholderEntity(
    @PrimaryKey val id: Long = 0
)
