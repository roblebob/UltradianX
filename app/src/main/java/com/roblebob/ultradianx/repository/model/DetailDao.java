package com.roblebob.ultradianx.repository.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Detail detail);

    @Query("SELECT * FROM Detail WHERE adventure = :adventure AND subject = 'detail'"  )
    LiveData<List<Detail>> loadDetailsForAdventure(String adventure);

    @Query("SELECT * FROM Detail WHERE adventure = :adventure AND subject = 'tag'"  )
    LiveData<List<Detail>> loadTagsForAdventure(String adventure);

    @Query("SELECT adventure FROM Detail")
    LiveData<List<String>> loadAdventures();
}
