package com.roblebob.ultradianx.repository.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(appState: AppState?)

    @Query("SELECT value FROM AppState WHERE `key` = :arg0")
    fun loadValueByKeyLive(key: String): LiveData<String>

    @Query("SELECT value FROM AppState WHERE `key` = :arg0")
    fun loadValueByKey(key: String): String?
}