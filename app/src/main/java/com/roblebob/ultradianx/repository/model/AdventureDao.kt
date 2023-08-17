package com.roblebob.ultradianx.repository.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AdventureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(adventure: Adventure)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(adventure: Adventure)

    @Delete
    fun delete(adventure: Adventure)

    @Query("SELECT COUNT(*) FROM Adventure")
    fun countAdventures(): Int

    @Query("SELECT COUNT(*) FROM Adventure WHERE title = :arg0")
    fun countAdventuresWithTitle(title: String): Int

    @Query("SELECT * FROM Adventure ORDER BY priority DESC")
    fun loadAdventureListLive(): LiveData<List<Adventure>>

    @Query("SELECT * FROM Adventure WHERE id = :arg0")
    fun loadAdventureByIdLive(id: Int): LiveData<Adventure>

    @Query("SELECT * FROM Adventure WHERE title = :arg0")
    fun loadAdventureByTitleLive(title: String): LiveData<Adventure>

    @Query("SELECT * FROM Adventure ORDER BY priority DESC")
    fun loadAdventureList(): List<Adventure>

    @Query("SELECT * FROM Adventure WHERE id = :arg0")
    fun loadAdventureById(id: Int): Adventure

    @Query("SELECT id FROM Adventure ORDER BY priority DESC")
    fun loadAdventureIdListLive(): LiveData<List<Int>>

    @Query("SELECT title FROM Adventure WHERE id = :arg0")
    fun loadAdventureTitleById(id: Int): String

    @Query(value = "UPDATE Adventure SET `priority` = :arg1 WHERE id = :arg0 ")
    fun updatePriority(id: Int, priority: Double)

    @Query(value = "UPDATE Adventure SET `active` = :arg1 WHERE id = :arg0 ")
    fun activate(id: Int, active: Boolean)
}