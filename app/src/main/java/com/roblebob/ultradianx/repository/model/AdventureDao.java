package com.roblebob.ultradianx.repository.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.roblebob.ultradianx.repository.model.Adventure;

import java.util.List;

@Dao
public interface AdventureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Adventure adventure);

    @Query("SELECT * FROM Adventure")
    LiveData<List<Adventure>> loadAdventureListLive();

    @Query("SELECT * FROM Adventure WHERE :title = title")
    LiveData<Adventure> loadAdventureLive( String title);

}
