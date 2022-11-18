package com.roblebob.ultradianx.repository.model;

import android.os.Bundle;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;


@Entity(tableName = "Adventure" , indices = @Index(value = {"title"}, unique = true))
public class Adventure {

    @PrimaryKey(autoGenerate = true )       private int     id;
    @ColumnInfo(name = "title")             private String  title;
    @ColumnInfo(name = "tags"       )       private String  tags;
    @ColumnInfo(name = "details")           private ArrayList<String>  details;


    public Adventure( String title, String tags, ArrayList<String> details) {
        this.title = title;
        this.tags = tags;
        this.details = details;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }

    public ArrayList<String> getDetails() {
        return details;
    }
    public void setDetails(ArrayList<String> details) {
        this.details = details;
    }


    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("id", this.id);
        bundle.putString("title", this.title);
        bundle.putString("tags", this.tags);
        bundle.putStringArrayList("details", this.details);
        return bundle;
    }
}
