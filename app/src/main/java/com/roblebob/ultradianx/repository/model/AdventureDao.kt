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

    @Query("SELECT COUNT(*) FROM Adventure WHERE title = :title")
    fun countAdventuresWithTitle(title: String): Int

    @Query("SELECT * FROM Adventure ORDER BY priority DESC")
    fun loadAdventureListLive(): LiveData<List<Adventure>>

    @Query("SELECT * FROM Adventure WHERE id = :id")
    fun loadAdventureByIdLive(id: Int): LiveData<Adventure>

    @Query("SELECT * FROM Adventure WHERE title = :title")
    fun loadAdventureByTitleLive(title: String): LiveData<Adventure>

    @Query("SELECT * FROM Adventure ORDER BY priority DESC")
    fun loadAdventureList(): List<Adventure>

    @Query("SELECT * FROM Adventure WHERE id = :id")
    fun loadAdventureById(id: Int): Adventure

    @Query("SELECT id FROM Adventure ORDER BY priority DESC")
    fun loadAdventureIdListLive(): LiveData<List<Int>>

    @Query("SELECT title FROM Adventure WHERE id = :id")
    fun loadAdventureTitleById(id: Int): String

    @Query(value = "UPDATE Adventure SET `priority` = :priority WHERE id = :id ")
    fun updatePriority(id: Int, priority: Double)

    @Query(value = "UPDATE Adventure SET active = :active WHERE id = :id ")
    fun activate(id: Int, active: Boolean)

    @Query(value = "DELETE FROM Adventure WHERE `priority` <= 0.0 AND `target` <= 0")
    fun cleanup()
}