package com.example.socialhelper.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InfoDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: Info)
    @Query("DELETE FROM user_info")
    suspend fun deleteInfo()
    @Query("SELECT * from user_info")
    fun getAllInfo(): LiveData<Info>
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(info: Info)
}