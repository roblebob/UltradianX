package com.roblebob.ultradianx.repository.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * This class represents a history entry.
 * The purpose of this table is to keep track of the history of the adventures.
 * They serve as an intermediate cache between the database and the clockify API.
 * So only those adventures that are completed (for the day or so)  and not yet uploaded to the
 * clockify API are stored in this table.
 */
@Entity(tableName = "History")
public class History {

    @PrimaryKey(autoGenerate = true)        private int     id;
    @ColumnInfo(name = "adventureId")       private int     adventureId;
    @ColumnInfo(name = "start")             private String  start;
    @ColumnInfo(name = "end")               private String  end;

    public History(int adventureId, String start, String end) {
        this.adventureId = adventureId;
        this.start = start;
        this.end = end;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getAdventureId() {
        return adventureId;
    }
    public void setAdventureId(int adventureId) {
        this.adventureId = adventureId;
    }

    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }
    public void setEnd(String end) {
        this.end = end;
    }


    @NonNull
    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", adventureId=" + adventureId +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
