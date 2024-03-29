package com.roblebob.ultradianx.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AppState")
public class AppState {
    @PrimaryKey(autoGenerate = true )     private int id;
    @ColumnInfo(name = "key")             private final String  key;
    @ColumnInfo(name = "value")           private String  value;

    public AppState(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }


    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
