package com.roblebob.ultradianx.repository.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
