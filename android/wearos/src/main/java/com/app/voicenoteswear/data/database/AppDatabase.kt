package com.app.voicenoteswear.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FilePath::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filePathDao(): FilePathDao
}
