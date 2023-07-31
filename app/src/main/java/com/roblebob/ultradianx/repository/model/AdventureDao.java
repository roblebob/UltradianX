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

    @Query("SELECT COUNT(*) FROM Adventure")
    int countAdventures();

    @Query("SELECT COUNT(*) FROM Adventure WHERE title=:title")
    int countAdventuresWithTitle(String title);

    @Query("SELECT * FROM Adventure ORDER BY priority DESC")
    LiveData<List<Adventure>> loadAdventureListLive();

    @Query("SELECT * FROM Adventure WHERE :id = id")
    LiveData<Adventure> loadAdventureByIdLive(int id);

    @Query("SELECT * FROM Adventure WHERE :title = title")
    LiveData<Adventure> loadAdventureByTitleLive(String title);


    @Query("SELECT * FROM Adventure ORDER BY priority DESC")
    List<Adventure> loadAdventureList();

    @Query("SELECT * FROM Adventure WHERE :id = id")
    Adventure loadAdventureById(int id);


    @Query("SELECT id FROM Adventure ORDER BY priority DESC")
    LiveData<List<Integer>> loadAdventureIdListLive();


    @Query("SELECT title FROM Adventure WHERE :id = id")
    String loadAdventureTitleById(int id);


    @Query(value = "UPDATE Adventure SET `lasttime` = :lasttime WHERE id = :id ")
    void updateLast(int id, String lasttime);

    @Query(value = "UPDATE Adventure SET `priority` = :priority WHERE id = :id ")
    void updatePriority(int id, double priority);

    @Query(value = "UPDATE Adventure SET `active` = :active WHERE id = :id ")
    void activate(int id, boolean active);
}
