package com.app.voicenoteswear.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FilePathDao {
    @Insert
    suspend fun insertFilePath(filePath: FilePath)

    @Query("SELECT * FROM file_paths")
    fun getAllFilePaths(): Flow<List<FilePath>>

    @Query("DELETE FROM file_paths")
    suspend fun deleteAllFilePaths()

    @Delete
    suspend fun deleteFilePath(filePath: FilePath)
}
