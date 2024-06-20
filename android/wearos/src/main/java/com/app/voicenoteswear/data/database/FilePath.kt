package com.app.voicenoteswear.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_paths")
data class FilePath(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val path: String
)
