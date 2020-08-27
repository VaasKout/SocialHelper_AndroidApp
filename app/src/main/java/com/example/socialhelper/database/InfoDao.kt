package com.example.socialhelper.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InfoDao{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(info: Info)
    @Query("DELETE FROM user_info")
    suspend fun deleteInfo()
    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateNote(info: Info)
    @Query("SELECT * from user_info")
    fun getAllInfo(): LiveData<Info>
}