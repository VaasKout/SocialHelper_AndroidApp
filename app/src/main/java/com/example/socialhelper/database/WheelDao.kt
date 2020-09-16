package com.example.socialhelper.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WheelDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wheelData: WheelData)

    @Query("SELECT * from wheel_data WHERE id = :key")
    fun selectData(key: Int): LiveData<WheelData>

    @Query("SELECT * from wheel_data")
    fun getAllData() : LiveData<List<WheelData>>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateData(wheelData: WheelData)

    @Delete
    suspend fun deleteData(wheelData: WheelData)

    @Query("DELETE FROM wheel_data")
    suspend fun deleteAll()
}