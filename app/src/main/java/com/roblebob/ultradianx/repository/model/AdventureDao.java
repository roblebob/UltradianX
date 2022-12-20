package com.roblebob.ultradianx.repository.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AdventureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Adventure adventure);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Adventure adventure);

    @Delete
    void delete(Adventure adventure);


    @Query("SELECT * FROM Adventure ORDER BY priority DESC")
    LiveData<List<Adventure>> loadAdventureListLive();

    @Query("SELECT * FROM Adventure WHERE :id = id")
    LiveData<Adventure> loadAdventureByIdLive(int id);

    @Query("SELECT * FROM Adventure WHERE :title = title")
    LiveData<Adventure> loadAdventureByTitleLive(String title);



    @Query(value = "UPDATE Adventure SET `last` = :last WHERE id = :id ")
    void updateLast(int id, String last);

    @Query(value = "UPDATE Adventure SET `priority` = :priority WHERE id = :id ")
    void updatePriority(int id, double priority);

}
