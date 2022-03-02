package com.roblebob.ultradianx;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AdventureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)    void insert(Adventure adventure);

    @Query("SELECT * FROM Adventure WHERE `immediate` = 'True'")
    LiveData<List<Adventure>> loadImmediateAdventureListLive();
}
