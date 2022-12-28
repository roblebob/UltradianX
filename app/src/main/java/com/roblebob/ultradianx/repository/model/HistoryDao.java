package com.roblebob.ultradianx.repository.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert( History history);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update( History history);

    @Delete
    void delete( History history);


    @Query("SELECT * FROM History WHERE :adventureId = adventureId ORDER BY `end` DESC")
    List<History> loadHistoryByAdventureId( int adventureId);

    @Query("SELECT * FROM History ORDER BY `end` DESC")
    List<History> loadEntireHistory( );


}
