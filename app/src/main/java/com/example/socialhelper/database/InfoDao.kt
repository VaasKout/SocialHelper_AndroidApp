package com.example.socialhelper.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * @see com.example.socialhelper.database.Info
 */
@Dao
interface InfoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInfo(info: Info)

    @Query("DELETE FROM user_info")
    suspend fun deleteInfo()

    @Query("SELECT * from user_info")
    fun getAllInfo(): LiveData<Info>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateInfo(info: Info)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWheel(wheelData: WheelData)

    @Query("SELECT * from wheel_data")
    fun getAllWheelData(): LiveData<List<WheelData>>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateWheel(wheelData: WheelData)

    @Query("DELETE FROM wheel_data")
    suspend fun deleteAllWheel()

    @Query("SELECT * from wheel_data WHERE id = :key")
    fun selectWheelData(key: Int): LiveData<WheelData>

}