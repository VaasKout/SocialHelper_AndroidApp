package com.example.socialhelper.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * @see com.example.socialhelper.database.WheelData
 */
@Dao
interface WheelDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wheelData: WheelData)

    @Query("SELECT * from wheel_data")
    fun getAllData() : LiveData<WheelData>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateData(wheelData: WheelData)

    @Query("DELETE FROM wheel_data")
    suspend fun deleteAll()
}