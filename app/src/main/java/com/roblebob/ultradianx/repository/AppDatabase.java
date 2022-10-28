package com.roblebob.ultradianx.repository;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.roblebob.ultradianx.model.Adventure;


@Database( entities = { Adventure.class},   version = 1,   exportSchema = false)
public abstract class AppDatabase extends RoomDatabase { /*singleton-pattern*/
    private static final String DATABASE_NAME  = "UltradianXAppDatabase";
    private static AppDatabase  sInstance;
    private static final Object LOCK  = new Object();
    public static AppDatabase   getInstance( Context context) {
        if ( sInstance == null) {  synchronized (LOCK) { sInstance = Room
                .databaseBuilder(  context.getApplicationContext(),  AppDatabase.class,  AppDatabase.DATABASE_NAME)
                .build();
        }}
        return sInstance;
    }
    public abstract AdventureDao adventureDao();
}
